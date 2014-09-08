package tools;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * A handy class to contain commonly used methods which are related to networks and the internet.
 * 
 * @author Sebastian Troy
 */
public class NetworkTools
	{
		/**
		 * <strong>Warning:</strong> Requires access to the internet.
		 * 
		 * @return - The external IP address of the current machine
		 */
		public static String getExternalIp()
			{
				URL whatismyip;
				BufferedReader in = null;
				String ipString = "";
				try
					{
						whatismyip = new URL("http://checkip.amazonaws.com");

						in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
						ipString = in.readLine();

					}
				catch (MalformedURLException e1)
					{
						e1.printStackTrace();
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
				finally
					{
						if (in != null)
							{
								try
									{
										in.close();
									}
								catch (IOException e)
									{
										e.printStackTrace();
									}
							}
					}
				return ipString;
			}

		/**
		 * If possible this method opens the default browser to the specified web page.
		 * If not it notifies the user of webpage's url so that they may access it
		 * manually.
		 * 
		 * @param url
		 *            - the URL of the webpage or html file which you wish to be opened.
		 */
		public static void openInBrowser(String url)
			{
				try
					{
						URI uri = new URL(url).toURI();

						Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
						if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
							desktop.browse(uri);
					}
				catch (Exception e)
					{
						/*
						 *  I know this is bad practice but we don't want to do anything clever for a specific error
						 */
						e.printStackTrace();

						// Copy to URL to the clipboard so the user can paste it into their browser
						StringSelection stringSelection = new StringSelection(url);
						Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
						clpbrd.setContents(stringSelection, null);
						// Notify the user of the failure
						WindowTools.informationWindow("This program just tried to open a webpage." + "\n"
							+ "The URL has been copied to your clipboard, simply paste into your browser to access.",
								"Webpage: " + url);
					}
			}
	}