package tools;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * A handy class to contain commonly used methods which are related to networks and the internet.
 * 
 * @author Sebastian Troy
 */
public class NetworkTools
	{
		/**
		 * @return - The local IPv4 address of the current machine
		 */
		public static String getLocalIP()
			{
				String ipString = "";

				try
					{
						ipString = Inet4Address.getLocalHost().getHostAddress();
					}
				catch (UnknownHostException e)
					{
						e.printStackTrace();
					}
				return ipString;
			}

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
				catch (UnknownHostException e)
					{
						ipString = "No Connection";
					}
				catch (MalformedURLException e)
					{
						e.printStackTrace();
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
		 * If possible this method opens the default browser to the specified web page. If not it notifies the user of webpage's url so that they may access it
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
						 * I know this is bad practice but we don't want to do anything clever for a specific error
						 */
						e.printStackTrace();

						// Copy to URL to the clipboard so the user can paste it into their browser
						StringSelection stringSelection = new StringSelection(url);
						Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
						clpbrd.setContents(stringSelection, null);
						// Notify the user of the failure
						WindowTools.informationWindow("This program just tried to open a webpage." + "\n" + "The URL has been copied to your clipboard, simply paste into your browser to access.",
								"Webpage: " + url);
					}
			}
	}
