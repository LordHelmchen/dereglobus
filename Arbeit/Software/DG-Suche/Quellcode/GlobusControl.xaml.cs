using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Net;
using System.Xml;

namespace DgSuche
{
    /// <summary>
    /// Interaktionslogik für GlobusControl.xaml
    /// </summary>
    public partial class GlobusControl : UserControl
    {
        // CSV oder XML
        private string IndexMode
        {
            get { return _comboBoxIndexMode.SelectionBoxItem.ToString(); }
        }

        private bool? KmlLinks
        {
            get { return _checkBoxKmlLinks.IsChecked; }
        }

        private bool? InOff
        {
            get { return _checkBoxInOff.IsChecked; }
        }

        private bool? Off
        {
            get { return _checkBoxOff.IsChecked; }
        }

        public GlobusControl()
        {
            InitializeComponent();

            List<string> artList = new List<string>() { "Alle", "Metropole", "Großstadt", "Stadt", "Kleinstadt", "Dorf", 
                "Festung", "Sakralbauwerk", "Ruine", "Handelsstätte", "Werkstätte", "Privathaus", "Rakshazar" };
            _comboBoxArt.ItemsSource = artList;
            _comboBoxArt.SelectedIndex = 0;

            if (Ortsmarke.ListOrtsmarken.Count <= 0)
                LoadIndexFile();
            else
                Filtern();
        }

        private void _buttonParseDG_Click(object sender, RoutedEventArgs e)
        {
            ParseKML_Files();
        }

        private void LoadIndexFile()
        {
            //Mouse.OverrideCursor = Cursors.Wait;

            DateTime timeStart = DateTime.Now;

            Ortsmarke.ListOrtsmarken.Clear();

            WebClient w = new WebClient();
            w.Encoding = System.Text.Encoding.UTF8;
            try
            {
                if (IndexMode == "XML")
                    Load_From_XML(w);
                else if (IndexMode == "CSV")
                    Load_From_CSV(w);

                _textBoxFilter.Text = string.Empty;
                Filtern();

                DateTime timeEnde = DateTime.Now;
                TimeSpan dauer = timeEnde - timeStart;
                _textBlockDauer.Text = "Ladedauer: " + dauer.ToString(@"mm\:ss\.fff");
            }
            catch (Exception ex)
            {
                MsgWindow errWin = new MsgWindow("DereGlobus Daten laden", "Beim Laden der DereGlobus Daten ist ein Fehler aufgetreten!", ex);
                errWin.ShowDialog();
            }

            //Mouse.OverrideCursor = Cursors.Arrow;
        }

        private void Load_From_CSV(WebClient w)
        {
            string link = string.Empty;
            if (KmlLinks == true)
                link = Properties.Settings.Default.DG_IndexPfad_CSV.Replace(".csv", "_MitLinks.csv");
            else
                link = Properties.Settings.Default.DG_IndexPfad_CSV;

            string s = w.DownloadString(link);
            System.IO.StringReader reader = new System.IO.StringReader(s);
            // Erste Zeile ignorieren, da Header
            reader.ReadLine();
            string line = null;
            string[] attributes;
            while (true)
            {
                line = reader.ReadLine();
                if (line != null)
                {
                    attributes = line.Split(';');

                    Ortsmarke lm = new Ortsmarke();
                    if (attributes != null && attributes.Length >= 1)
                        lm.Name = attributes[0];
                    if (attributes != null && attributes.Length >= 2)
                        lm.Art = attributes[1];
                    if (attributes != null && attributes.Length >= 3)
                        lm.Longitude = attributes[2];
                    if (attributes != null && attributes.Length >= 4)
                        lm.Latitude = attributes[3];
                    if (attributes != null && attributes.Length >= 5)
                        lm.Link = attributes[4];
                    if (attributes != null && attributes.Length >= 6)
                        lm.KmlLink = attributes[5];

                    Ortsmarke.ListOrtsmarken.Add(lm);
                }
                else
                    break; // Ende der Datei erreicht
            }
        }

        private void Load_From_XML(WebClient w)
        {
            string link = string.Empty;
            if (KmlLinks == true)
                link = Properties.Settings.Default.DG_IndexPfad_XML.Replace(".xml", "_MitLinks.xml");
            else
                link = Properties.Settings.Default.DG_IndexPfad_XML;

            string s = w.DownloadString(link);
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(s);
            string xmlns = string.Empty;
            XmlNodeList landmarken;
            landmarken = doc.SelectNodes("//Ortsmarken/Ortsmarke");

            _listBoxOrtsmarken.BeginInit();
            foreach (XmlNode node in landmarken)
            {
                Ortsmarke lm = new Ortsmarke();
                if (node.Attributes["Name"] != null)
                    lm.Name = node.Attributes["Name"].InnerText;
                if (node.Attributes["Art"] != null)
                    lm.Art = node.Attributes["Art"].InnerText;
                if (node.Attributes["Longitude"] != null)
                    lm.Longitude = node.Attributes["Longitude"].InnerText;
                if (node.Attributes["Latitude"] != null)
                    lm.Latitude = node.Attributes["Latitude"].InnerText;
                if (node.Attributes["KmlLink"] != null)
                    lm.KmlLink = node.Attributes["KmlLink"].InnerText;

                Ortsmarke.ListOrtsmarken.Add(lm);
            }
        }

        private void ParseKML_Files()
        {
            Mouse.OverrideCursor = Cursors.Wait;

            DateTime timeStart = DateTime.Now;

            Ortsmarke.ListOrtsmarken.Clear();

            WebClient w = new WebClient();
            w.Encoding = System.Text.Encoding.UTF8;
            try
            {
                string s = w.DownloadString("http://www.dereglobus.orkenspalter.de/public/DereGlobus/Staedte/kml/Siedlungen/Siedlungen.kml");
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(s);
                string xmlns = string.Empty;
                XmlNodeList linksSiedlungenFolder;
                XmlNamespaceManager nsmgr;
                if (doc.DocumentElement.Attributes["xmlns"] != null)
                {
                    xmlns = doc.DocumentElement.Attributes["xmlns"].Value;
                    nsmgr = new XmlNamespaceManager(doc.NameTable);
                    nsmgr.AddNamespace("x", xmlns);

                    linksSiedlungenFolder = doc.SelectNodes("//x:Folder/x:NetworkLink/x:Link/x:href", nsmgr);
                }
                else
                {
                    linksSiedlungenFolder = doc.SelectNodes("//Folder/NetworkLink/Link/href");
                }

                string indexString = string.Empty;
                if (IndexMode == "XML")
                {
                    indexString += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Environment.NewLine;
                    indexString += string.Format("<Ortsmarken Aktualisiert=\"{0}\">", timeStart.ToString()) + Environment.NewLine;
                }
                else if (IndexMode == "CSV")
                {
                    indexString += "Name;Art;Longitude;Latitude;Link" + Environment.NewLine;
                }

                foreach (XmlNode nodeSiedlungenLink in linksSiedlungenFolder)
                {
                    string linkSiedlungen = nodeSiedlungenLink.InnerText;
                    s = w.DownloadString(linkSiedlungen);
                    doc = new XmlDocument();
                    doc.LoadXml(s);
                    XmlNodeList list;
                    if (doc.DocumentElement.Attributes["xmlns"] != null)
                    {
                        xmlns = doc.DocumentElement.Attributes["xmlns"].Value;
                        nsmgr = new XmlNamespaceManager(doc.NameTable);
                        nsmgr.AddNamespace("x", xmlns);

                        list = doc.SelectNodes("//x:Placemark", nsmgr);
                    }
                    else
                    {
                        list = doc.SelectNodes("//Placemark");
                    }
                    foreach (XmlNode node in list)
                    {
                        string point = string.Empty;
                        if (node["Point"] != null)
                            point = node["Point"].InnerText;
                        string[] coordinates = point.Split(',');

                        // Link
                        string link = string.Empty;
                        XmlNode linkNode;
                        if (node["ExtendedData"] != null && (linkNode = node["ExtendedData"]["Data"]) != null)
                        {
                            XmlAttribute at = linkNode.Attributes["name"];
                            if (at != null && at.Value == "Link" && linkNode["value"] != null)
                            {
                                if (linkNode["value"].InnerText != "$[name]")
                                    link = linkNode["value"].InnerText;
                            }
                        }

                        Ortsmarke lm = new Ortsmarke()
                        {
                            Name = node["name"].InnerText.Trim(),
                            Longitude = (coordinates.Length >= 1 ? coordinates[0].Trim() : string.Empty),
                            Latitude = (coordinates.Length >= 2 ? coordinates[1].Trim() : string.Empty),
                            KmlLink = linkSiedlungen,
                            Art = node["styleUrl"].InnerText.Split('#')[1].Trim(),
                            Link = link
                        };

                        Ortsmarke.ListOrtsmarken.Add(lm);

                        if (IndexMode == "XML")
                            indexString += lm.ToXML((bool)KmlLinks) + Environment.NewLine;
                        else if (IndexMode == "CSV")
                            indexString += lm.ToCSV((bool)KmlLinks) + Environment.NewLine;
                    }
                }
                _listBoxOrtsmarken.BeginInit();
                _listBoxOrtsmarken.ItemsSource = Ortsmarke.ListOrtsmarken;
                _textBoxFilter.Text = string.Empty;
                _listBoxOrtsmarken.Items.SortDescriptions.Add(new System.ComponentModel.SortDescription("Name", System.ComponentModel.ListSortDirection.Ascending));
                textBlock3.Text = Ortsmarke.ListOrtsmarken.Count + " Ortsmarken";
                _listBoxOrtsmarken.EndInit();

                DateTime timeEnde = DateTime.Now;
                TimeSpan dauer = timeEnde - timeStart;
                _textBlockDauer.Text = "Ladedauer: " + dauer.ToString(@"mm\:ss\.fff");

                if (IndexMode == "XML")
                {
                    indexString += Environment.NewLine + "</Ortsmarken>";
                    System.Xml.XmlDocument xmlIndexDoc = new XmlDocument();
                    xmlIndexDoc.LoadXml(indexString);
                    xmlIndexDoc.Save("Index.xml");
                }
                else if (IndexMode == "CSV")
                {
                    System.IO.TextWriter csvWriter = System.IO.File.CreateText("Index.csv");
                    csvWriter.Write(indexString);
                    csvWriter.Close();
                }

                MessageBox.Show("Die Ortsmarken-Daten wurden vom DereGlobus extrahiert!\n\nDie Index-Datei wurde im Programm-Verzeichnis gespeichert.");
            }
            catch (Exception ex)
            {
                MsgWindow errWin = new MsgWindow("DereGlobus Daten laden", "Beim Laden der DereGlobus Daten ist ein Fehler aufgetreten!", ex);
                errWin.ShowDialog();
            }

            Mouse.OverrideCursor = Cursors.Arrow;
        }

        private void _textBoxFilter_TextChanged(object sender, TextChangedEventArgs e)
        {
            Filtern();
        }

        private void Filtern()
        {
            string art = _comboBoxArt.SelectedValue.ToString();
            switch (art)
            {
                case "Großstadt":
                    art = "Grossstadt";
                    break;
                case "Sakralbauwerk":
                    art = "Sonstige_Sakralbauwerk";
                    break;
                case "Ruine":
                    art = "Sonstige_Ruine";
                    break;
                case "Handelsstätte":
                    art = "Sonstige_Handelsstaette";
                    break;
                case "Werkstätte":
                    art = "Sonstige_Werkstaette";
                    break;
                case "Privathaus":
                    art = "Sonstige_Privathaus";
                    break;
                default:
                    break;
            }
            string txtOrig = _textBoxFilter.Text;
            string upper = txtOrig.ToUpper();
            string lower = txtOrig.ToLower();

            _listBoxOrtsmarken.BeginInit();

            var orteFiltered = from ort in Ortsmarke.ListOrtsmarken
                               let ortName = ort.Name
                               where
                                 ortName.ToLower().Contains(lower)
                                 && (art == "Alle" ? true       // Art
                                    : ort.Art.Contains(art))
                                 && (InOff == true ? true       // Inoffiziell
                                    : !(ort.Name.ToLower().Contains("inoff") || ort.Art.Contains("Rakshazar")))
                                 && (Off == true ? true         // Offiziell
                                    : (ort.Name.ToLower().Contains("inoff") || ort.Art.Contains("Rakshazar")))
                               select ort;
            _listBoxOrtsmarken.ItemsSource = orteFiltered;

            _listBoxOrtsmarken.Items.SortDescriptions.Add(new System.ComponentModel.SortDescription("Name", System.ComponentModel.ListSortDirection.Ascending));
            if (_listBoxOrtsmarken.Items != null)
                textBlock3.Text = _listBoxOrtsmarken.Items.Count + " Ortsmarke" + (_listBoxOrtsmarken.Items.Count != 1 ? "n" : string.Empty);
            else
                textBlock3.Text = "0 Ortsmarken";
            _listBoxOrtsmarken.EndInit();
        }

        private void Hyperlink_RequestNavigate(object sender, RequestNavigateEventArgs e)
        {
            System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo(e.Uri.AbsoluteUri));

            e.Handled = true;
        }

        private void _comboBoxArt_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            Filtern();
        }

        public object SelectedItem
        {
            get { return _listBoxOrtsmarken.SelectedItem; }
        }

        public ListBox ListBoxOrtsmarken
        {
            get { return _listBoxOrtsmarken; }
        }

        private void _buttonadenDG_Click(object sender, RoutedEventArgs e)
        {
            LoadIndexFile();
        }

        private void CheckBoxInOff_UnChecked(object sender, RoutedEventArgs e)
        {
            if (IsInitialized && IsLoaded)
                Filtern();
        }

        private void CheckBoxOff_UnChecked(object sender, RoutedEventArgs e)
        {
            if (IsInitialized && IsLoaded)
                Filtern();
        }
    }
}
