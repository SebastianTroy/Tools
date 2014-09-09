package tools.server;

import java.io.Serializable;

final class TString implements Serializable
	{
		private static final long serialVersionUID = 1L;

		final String string;

		TString(String string)
			{

				this.string = string;
			}
	}
