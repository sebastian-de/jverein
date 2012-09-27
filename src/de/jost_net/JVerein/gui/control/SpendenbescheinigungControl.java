/**********************************************************************
 * $Source$
 * $Revision$
 * $Date$
 * $Author$
 *
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
package de.jost_net.JVerein.gui.control;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

import de.jost_net.JVerein.Einstellungen;
import de.jost_net.JVerein.JVereinPlugin;
import de.jost_net.JVerein.Variable.AllgemeineMap;
import de.jost_net.JVerein.gui.action.SpendenbescheinigungAction;
import de.jost_net.JVerein.gui.action.SpendenbescheinigungPrintAction;
import de.jost_net.JVerein.gui.input.FormularInput;
import de.jost_net.JVerein.gui.menu.SpendenbescheinigungMenu;
import de.jost_net.JVerein.io.FormularAufbereitung;
import de.jost_net.JVerein.keys.Formularart;
import de.jost_net.JVerein.keys.HerkunftSpende;
import de.jost_net.JVerein.keys.Spendenart;
import de.jost_net.JVerein.rmi.Formular;
import de.jost_net.JVerein.rmi.Spendenbescheinigung;
import de.jost_net.JVerein.util.Dateiname;
import de.jost_net.JVerein.util.JVDateFormatTTMMJJJJ;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class SpendenbescheinigungControl extends AbstractControl
{

  private de.willuhn.jameica.system.Settings settings;

  private TablePart spbList;

  private SelectInput spendenart;

  private TextInput zeile1;

  private TextInput zeile2;

  private TextInput zeile3;

  private TextInput zeile4;

  private TextInput zeile5;

  private TextInput zeile6;

  private TextInput zeile7;

  private DateInput spendedatum;

  private DateInput bescheinigungsdatum;

  private DecimalInput betrag;

  private FormularInput formular;

  private CheckboxInput ersatzaufwendungen;

  private TextInput bezeichnungsachzuwendung;

  private SelectInput herkunftspende;

  private CheckboxInput unterlagenwertermittlung;

  private Spendenbescheinigung spendenbescheinigung;

  public SpendenbescheinigungControl(AbstractView view)
  {
    super(view);
    settings = new de.willuhn.jameica.system.Settings(this.getClass());
    settings.setStoreWhenRead(true);
  }

  public Spendenbescheinigung getSpendenbescheinigung()
  {
    if (spendenbescheinigung != null)
    {
      return spendenbescheinigung;
    }
    spendenbescheinigung = (Spendenbescheinigung) getCurrentObject();
    return spendenbescheinigung;
  }

  public SelectInput getSpendenart() throws RemoteException
  {
    if (spendenart != null)
    {
      return spendenart;
    }
    spendenart = new SelectInput(Spendenart.getArray(), new Spendenart(
        getSpendenbescheinigung().getSpendenart()));
    spendenart.addListener(new Listener()
    {
      public void handleEvent(Event event)
      {
        enableSachspende();
      }
    });
    return spendenart;
  }

  private void enableSachspende()
  {
    try
    {
      Spendenart spa = (Spendenart) getSpendenart().getValue();
      getBezeichnungSachzuwendung().setEnabled(
          spa.getKey() == Spendenart.SACHSPENDE);
      getHerkunftSpende().setEnabled(spa.getKey() == Spendenart.SACHSPENDE);
      getUnterlagenWertermittlung().setEnabled(
          spa.getKey() == Spendenart.SACHSPENDE);
    }
    catch (RemoteException e)
    {
      Logger.error(JVereinPlugin.getI18n().tr("Fehler"), e);
    }
  }

  public TextInput getZeile1(boolean withFocus) throws RemoteException
  {
    if (zeile1 != null)
    {
      return zeile1;
    }
    zeile1 = new TextInput(getSpendenbescheinigung().getZeile1(), 40);
    if (withFocus)
    {
      zeile1.focus();
    }
    return zeile1;
  }

  public TextInput getZeile2() throws RemoteException
  {
    if (zeile2 != null)
    {
      return zeile2;
    }
    zeile2 = new TextInput(getSpendenbescheinigung().getZeile2(), 40);
    return zeile2;
  }

  public TextInput getZeile3() throws RemoteException
  {
    if (zeile3 != null)
    {
      return zeile3;
    }
    zeile3 = new TextInput(getSpendenbescheinigung().getZeile3(), 40);
    return zeile3;
  }

  public TextInput getZeile4() throws RemoteException
  {
    if (zeile4 != null)
    {
      return zeile4;
    }
    zeile4 = new TextInput(getSpendenbescheinigung().getZeile4(), 40);
    return zeile4;
  }

  public TextInput getZeile5() throws RemoteException
  {
    if (zeile5 != null)
    {
      return zeile5;
    }
    zeile5 = new TextInput(getSpendenbescheinigung().getZeile5(), 40);
    return zeile5;
  }

  public TextInput getZeile6() throws RemoteException
  {
    if (zeile6 != null)
    {
      return zeile6;
    }
    zeile6 = new TextInput(getSpendenbescheinigung().getZeile6(), 40);
    return zeile6;
  }

  public TextInput getZeile7() throws RemoteException
  {
    if (zeile7 != null)
    {
      return zeile7;
    }
    zeile7 = new TextInput(getSpendenbescheinigung().getZeile7(), 40);
    return zeile7;
  }

  public DateInput getSpendedatum() throws RemoteException
  {
    if (spendedatum != null)
    {
      return spendedatum;
    }
    spendedatum = new DateInput(getSpendenbescheinigung().getSpendedatum());
    return spendedatum;
  }

  public DateInput getBescheinigungsdatum() throws RemoteException
  {
    if (bescheinigungsdatum != null)
    {
      return bescheinigungsdatum;
    }
    bescheinigungsdatum = new DateInput(getSpendenbescheinigung()
        .getBescheinigungsdatum());
    return bescheinigungsdatum;
  }

  public DecimalInput getBetrag() throws RemoteException
  {
    if (betrag != null)
    {
      return betrag;
    }
    betrag = new DecimalInput(getSpendenbescheinigung().getBetrag(),
        Einstellungen.DECIMALFORMAT);
    return betrag;
  }

  public SelectInput getFormular() throws RemoteException
  {
    if (formular != null)
    {
      return formular;
    }
    String def = null;
    if (getSpendenbescheinigung().getFormular() != null)
    {
      def = getSpendenbescheinigung().getFormular().getID();
    }
    if (getSpendenbescheinigung().getBuchungen() != null
        && getSpendenbescheinigung().getBuchungen().size() > 1)
    {
      formular = new FormularInput(Formularart.SAMMELSPENDENBESCHEINIGUNG, def);
    }
    else
    {
      formular = new FormularInput(Formularart.SPENDENBESCHEINIGUNG, def);
    }
    return formular;
  }

  public CheckboxInput getErsatzAufwendungen() throws RemoteException
  {
    if (ersatzaufwendungen != null)
    {
      return ersatzaufwendungen;
    }
    ersatzaufwendungen = new CheckboxInput(getSpendenbescheinigung()
        .getErsatzAufwendungen());
    return ersatzaufwendungen;
  }

  public TextInput getBezeichnungSachzuwendung() throws RemoteException
  {
    if (bezeichnungsachzuwendung != null)
    {
      return bezeichnungsachzuwendung;
    }
    bezeichnungsachzuwendung = new TextInput(getSpendenbescheinigung()
        .getBezeichnungSachzuwendung(), 100);
    enableSachspende();
    return bezeichnungsachzuwendung;
  }

  public SelectInput getHerkunftSpende() throws RemoteException
  {
    if (herkunftspende != null)
    {
      return herkunftspende;
    }
    herkunftspende = new SelectInput(HerkunftSpende.getArray(),
        new HerkunftSpende(getSpendenbescheinigung().getHerkunftSpende()));
    enableSachspende();
    return herkunftspende;
  }

  public CheckboxInput getUnterlagenWertermittlung() throws RemoteException
  {
    if (unterlagenwertermittlung != null)
    {
      return unterlagenwertermittlung;
    }
    unterlagenwertermittlung = new CheckboxInput(getSpendenbescheinigung()
        .getUnterlagenWertermittlung());
    enableSachspende();
    return unterlagenwertermittlung;
  }

  /**
   * This method stores the project using the current values.
   */
  public void handleStore()
  {
    try
    {
      Spendenbescheinigung spb = getSpendenbescheinigung();
      Spendenart spa = (Spendenart) getSpendenart().getValue();
      spb.setSpendenart(spa.getKey());
      spb.setZeile1((String) getZeile1(false).getValue());
      spb.setZeile2((String) getZeile2().getValue());
      spb.setZeile3((String) getZeile3().getValue());
      spb.setZeile4((String) getZeile4().getValue());
      spb.setZeile5((String) getZeile5().getValue());
      spb.setZeile6((String) getZeile6().getValue());
      spb.setZeile7((String) getZeile7().getValue());
      spb.setSpendedatum((Date) getSpendedatum().getValue());
      spb.setBescheinigungsdatum((Date) getBescheinigungsdatum().getValue());
      spb.setBetrag((Double) getBetrag().getValue());
      spb.setErsatzAufwendungen((Boolean) getErsatzAufwendungen().getValue());
      spb.setBezeichnungSachzuwendung((String) getBezeichnungSachzuwendung()
          .getValue());
      spb.setFormular((Formular) getFormular().getValue());
      HerkunftSpende hsp = (HerkunftSpende) getHerkunftSpende().getValue();
      spb.setHerkunftSpende(hsp.getKey());
      spb.setUnterlagenWertermittlung((Boolean) getUnterlagenWertermittlung()
          .getValue());
      spb.store();

      GUI.getStatusBar().setSuccessText(
          JVereinPlugin.getI18n().tr("Spendenbescheinigung gespeichert"));
    }
    catch (ApplicationException e)
    {
      GUI.getStatusBar().setErrorText(e.getMessage());
    }
    catch (RemoteException e)
    {
      String fehler = JVereinPlugin.getI18n().tr(
          "Fehler bei Speichern der Spendenbescheinigung");
      Logger.error(fehler, e);
      GUI.getStatusBar().setErrorText(fehler);
    }
  }

  public Button getPDFStandardButton()
  {
    Button b = new Button(JVereinPlugin.getI18n().tr("PDF (Standard)"),
        new Action()
        {
          /**
           * Diese Action verwendet die "SpendenbescheinigungPrintAction" f�r
           * die Aufbereitung des Dokumentes. Als Rahmen ist der Dialog zur
           * Dateiauswahl und die Anzeige des Dokumentes um die Generierung
           * gesetzt.
           */
          public void handleAction(Object context) throws ApplicationException
          {
            try
            {
              Spendenbescheinigung spb = getSpendenbescheinigung();
              if (spb.isNewObject())
              {
                GUI.getStatusBar().setErrorText(
                    JVereinPlugin.getI18n().tr(
                        "Spendenbescheinigung bitte erst speichern!"));
                return;
              }
              FileDialog fd = new FileDialog(GUI.getShell(), SWT.SAVE);
              fd.setText(JVereinPlugin.getI18n().tr("Ausgabedatei w�hlen."));
              String path = Einstellungen.getEinstellung()
                  .getSpendenbescheinigungverzeichnis();
              if (path != null && path.length() > 0)
              {
                fd.setFilterPath(path);
              }
              if (spb.getMitglied() != null)
              {
                fd.setFileName(new Dateiname(spb.getMitglied(), spb
                    .getBescheinigungsdatum(), JVereinPlugin.getI18n().tr(
                    "Spendenbescheinigung"), Einstellungen.getEinstellung()
                    .getDateinamenmusterSpende(), "pdf").get());
              }
              else
              {
                fd.setFileName(new Dateiname(spb.getZeile1(), spb.getZeile2(),
                    spb.getBescheinigungsdatum(), JVereinPlugin.getI18n().tr(
                        "Spendenbescheinigung"), Einstellungen.getEinstellung()
                        .getDateinamenmusterSpende(), "pdf").get());
              }
              fd.setFilterExtensions(new String[] { "*.pdf" });

              String s = fd.open();
              if (s == null || s.length() == 0)
              {
                return;
              }
              if (!s.endsWith(".pdf"))
              {
                s = s + ".pdf";
              }
              final File file = new File(s);
              //
              SpendenbescheinigungPrintAction spa = new SpendenbescheinigungPrintAction(
                  true, s);
              spa.handleAction(spb);
              GUI.getStatusBar().setSuccessText(
                  JVereinPlugin.getI18n().tr("Spendenbescheinigung erstellt"));
              GUI.getDisplay().asyncExec(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    new Program().handleAction(file);
                  }
                  catch (ApplicationException ae)
                  {
                    Application.getMessagingFactory().sendMessage(
                        new StatusBarMessage(ae.getLocalizedMessage(),
                            StatusBarMessage.TYPE_ERROR));
                  }
                }
              });
            }
            catch (RemoteException e)
            {
              Logger.error(e.getMessage());
              throw new ApplicationException(JVereinPlugin.getI18n().tr(
                  "Fehler bei der Aufbereitung der Spendenbescheinigung"));
            }
          }
        }, null, false, "acroread.png");
    return b;
  }

  public Button getPDFIndividuellButton()
  {
    Button b = new Button(JVereinPlugin.getI18n().tr("PDF (Individuell)"),
        new Action()
        {
          public void handleAction(Object context) throws ApplicationException
          {
            try
            {
              generiereSpendenbescheinigungIndividuell();
            }
            catch (RemoteException e)
            {
              Logger.error(e.getMessage());
              throw new ApplicationException(JVereinPlugin.getI18n().tr(
                  "Fehler bei der Aufbereitung der Spendenbescheinigung"));
            }
            catch (IOException e)
            {
              Logger.error(e.getMessage());
              throw new ApplicationException(JVereinPlugin.getI18n().tr(
                  "Fehler bei der Aufbereitung der Spendenbescheinigung"));
            }
          }
        }, null, false, "acroread.png");
    return b;
  }

  private void generiereSpendenbescheinigungIndividuell() throws IOException
  {
    Spendenbescheinigung spb = getSpendenbescheinigung();
    if (spb.isNewObject())
    {
      GUI.getStatusBar().setErrorText(
          JVereinPlugin.getI18n().tr(
              "Spendenbescheinigung bitte erst speichern!"));
      return;
    }
    FileDialog fd = new FileDialog(GUI.getShell(), SWT.SAVE);
    fd.setText(JVereinPlugin.getI18n().tr("Ausgabedatei w�hlen."));
    String path = Einstellungen.getEinstellung()
        .getSpendenbescheinigungverzeichnis();
    if (path != null && path.length() > 0)
    {
      fd.setFilterPath(path);
    }
    if (spb.getMitglied() != null)
    {
      fd.setFileName(new Dateiname(spb.getMitglied(), spb
          .getBescheinigungsdatum(), JVereinPlugin.getI18n().tr(
          "Spendenbescheinigung"), Einstellungen.getEinstellung()
          .getDateinamenmusterSpende(), "pdf").get());
    }
    else
    {
      fd.setFileName(new Dateiname(spb.getZeile1(), spb.getZeile2(), spb
          .getBescheinigungsdatum(), "Spendenbescheinigung", Einstellungen
          .getEinstellung().getDateinamenmusterSpende(), "pdf").get());
    }
    fd.setFilterExtensions(new String[] { "*.pdf" });

    String s = fd.open();
    if (s == null || s.length() == 0)
    {
      return;
    }
    if (!s.endsWith(".pdf"))
    {
      s = s + ".pdf";
    }
    final File file = new File(s);
    settings.setAttribute("lastdir", file.getParent());

    /* Check ob auch ein Formular ausgewaehlt ist */
    Formular spendeformular = getSpendenbescheinigung().getFormular();
    if (spendeformular == null)
    {
      GUI.getStatusBar().setErrorText(JVereinPlugin.getI18n().tr("Bitte Formular auswaehlen"));
      return;
    }

    Formular fo = (Formular) Einstellungen.getDBService().createObject(
        Formular.class, spendeformular.getID());
    Map<String, Object> map = getSpendenbescheinigung().getMap(null);
    map = new AllgemeineMap().getMap(map);
    FormularAufbereitung fa = new FormularAufbereitung(file);
    fa.writeForm(fo, map);
    fa.showFormular();

  }

  public Part getSpendenbescheinigungList() throws RemoteException
  {
    DBService service = Einstellungen.getDBService();
    DBIterator spendenbescheinigungen = service
        .createList(Spendenbescheinigung.class);
    spendenbescheinigungen.setOrder("ORDER BY bescheinigungsdatum desc");

    spbList = new TablePart(spendenbescheinigungen,
        new SpendenbescheinigungAction());
    spbList.addColumn(JVereinPlugin.getI18n().tr("Bescheinigungsdatum"), "bescheinigungsdatum",
        new DateFormatter(new JVDateFormatTTMMJJJJ()));
    spbList.addColumn(JVereinPlugin.getI18n().tr("Spendedatum"), "spendedatum", new DateFormatter(
        new JVDateFormatTTMMJJJJ()));
    spbList.addColumn(JVereinPlugin.getI18n().tr("Betrag"), "betrag", new CurrencyFormatter("",
        Einstellungen.DECIMALFORMAT));
    spbList.addColumn(JVereinPlugin.getI18n().tr("Zeile 1"), "zeile1");
    spbList.addColumn(JVereinPlugin.getI18n().tr("Zeile 2"), "zeile2");
    spbList.addColumn(JVereinPlugin.getI18n().tr("Zeile 3"), "zeile3");
    spbList.addColumn(JVereinPlugin.getI18n().tr("Zeile 4"), "zeile4");
    spbList.addColumn(JVereinPlugin.getI18n().tr("Zeile 5"), "zeile5");
    spbList.addColumn(JVereinPlugin.getI18n().tr("Zeile 6"), "zeile6");
    spbList.addColumn(JVereinPlugin.getI18n().tr("Zeile 7"), "zeile7");

    spbList.setRememberColWidths(true);
    spbList.setContextMenu(new SpendenbescheinigungMenu());
    spbList.setRememberOrder(true);
    spbList.setSummary(false);
    spbList.setMulti(true);
    return spbList;
  }

  public void refreshTable() throws RemoteException
  {
    spbList.removeAll();
    DBIterator spendenbescheinigungen = Einstellungen.getDBService()
        .createList(Spendenbescheinigung.class);
    spendenbescheinigungen.setOrder("ORDER BY bescheinigungsdatum desc");
    while (spendenbescheinigungen.hasNext())
    {
      spbList.addItem(spendenbescheinigungen.next());
    }
  }

}
