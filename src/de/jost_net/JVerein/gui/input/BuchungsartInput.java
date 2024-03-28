/**********************************************************************
 * Copyright (c) by Heiner Jostkleigrewe
 * This program is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
 *  the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, 
 * see <http://www.gnu.org/licenses/>.
 * 
 * heiner@jverein.de
 * www.jverein.de
 **********************************************************************/
package de.jost_net.JVerein.gui.input;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.jost_net.JVerein.Einstellungen;
import de.jost_net.JVerein.keys.BuchungBuchungsartAuswahl;
import de.jost_net.JVerein.keys.BuchungsartSort;
import de.jost_net.JVerein.rmi.Buchungsart;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.gui.input.AbstractInput;
import de.willuhn.jameica.gui.input.SelectInput;

public class BuchungsartInput
{
  private int unterdrueckunglaenge = 0;
  
  public AbstractInput getBuchungsartInput(AbstractInput buchungsart,
      Buchungsart bart) throws RemoteException
  {
    switch (Einstellungen.getEinstellung().getBuchungBuchungsartAuswahl())
    {
      case BuchungBuchungsartAuswahl.ComboBox:
        unterdrueckunglaenge = Einstellungen.getEinstellung().getUnterdrueckungLaenge();
        if (unterdrueckunglaenge > 0) 
        {
          final DBService service = Einstellungen.getDBService();
          Calendar cal = Calendar.getInstance();
          Date db = cal.getTime();
          cal.add(Calendar.MONTH, - unterdrueckunglaenge);
          Date dv = cal.getTime();
          String sql = "SELECT DISTINCT buchungsart.* from buchungsart, buchung ";
          sql += "WHERE buchung.buchungsart = buchungsart.id ";
          sql += "AND buchung.datum >= ? AND buchung.datum <= ? ";
          if (Einstellungen.getEinstellung()
              .getBuchungsartSort() == BuchungsartSort.NACH_NUMMER)
          {
            sql += "ORDER BY nummer";
          }
          else
          {
            sql += "ORDER BY bezeichnung";
          }
          ResultSetExtractor rs = new ResultSetExtractor()
          {
            @Override
            public Object extract(ResultSet rs) throws RemoteException, SQLException
            {
              ArrayList<Buchungsart> list = new ArrayList<Buchungsart>();
              while (rs.next())
              {
                list.add(
                  (Buchungsart) service.createObject(Buchungsart.class, rs.getString(1)));
              }
              return list;
            }
          };
          @SuppressWarnings("unchecked")
          ArrayList<Buchungsart> ergebnis = (ArrayList<Buchungsart>) service.execute(sql,
              new Object[] { dv, db }, rs);
          buchungsart = new SelectInput(ergebnis.toArray(), bart);
        }
        else
        {
          DBIterator<Buchungsart> it = Einstellungen.getDBService()
              .createList(Buchungsart.class);
          if (Einstellungen.getEinstellung()
              .getBuchungsartSort() == BuchungsartSort.NACH_NUMMER)
          {
            it.setOrder("ORDER BY nummer");
          }
          else
          {
            it.setOrder("ORDER BY bezeichnung");
          }
          buchungsart = new SelectInput(it != null ? PseudoIterator.asList(it) : null, bart);
        }
        
        switch (Einstellungen.getEinstellung().getBuchungsartSort())
        {
          case BuchungsartSort.NACH_NUMMER:
            ((SelectInput) buchungsart).setAttribute("nrbezeichnung");
            break;
          case BuchungsartSort.NACH_BEZEICHNUNG_NR:
            ((SelectInput) buchungsart).setAttribute("bezeichnungnr");
            break;
          default:
            ((SelectInput) buchungsart).setAttribute("bezeichnung");
            break;
        }
        ((SelectInput) buchungsart).setPleaseChoose("Bitte ausw�hlen");
        break;
      case BuchungBuchungsartAuswahl.SearchInput:
      default: // default soll SearchInput sein. Eigentlich sollten die
        // Settings immer gesetzt sein, aber man weiss ja nie.
        buchungsart = new BuchungsartSearchInput();
        switch (Einstellungen.getEinstellung().getBuchungsartSort())
        {
          case BuchungsartSort.NACH_NUMMER:
            ((BuchungsartSearchInput) buchungsart).setAttribute("nrbezeichnung");
            break;
          case BuchungsartSort.NACH_BEZEICHNUNG_NR:
            ((BuchungsartSearchInput) buchungsart).setAttribute("bezeichnungnr");
            break;
          default:
            ((BuchungsartSearchInput) buchungsart).setAttribute("bezeichnung");
            break;
        }
        ((BuchungsartSearchInput) buchungsart)
            .setSearchString("Zum Suchen tippen ...");
    }
    buchungsart.setValue(bart);
    return buchungsart;
  }

}
