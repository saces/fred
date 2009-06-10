package freenet.clients.http;

import java.io.IOException;
import java.net.URI;

import freenet.client.HighLevelSimpleClient;
import freenet.l10n.L10n;
import freenet.node.useralerts.UserAlertManager;
import freenet.support.HTMLNode;
import freenet.support.api.HTTPRequest;

/** This is just documentation, it will be replaced with a plugin wizard eventually. */
public class InsertFreesiteToadlet extends Toadlet {

	private final UserAlertManager alerts;
	
	protected InsertFreesiteToadlet(HighLevelSimpleClient client, UserAlertManager alerts) {
		super(client);
		this.alerts = alerts;
	}
	
	@Override
    public void handleGet(URI uri, HTTPRequest req, ToadletContext ctx) throws ToadletContextClosedException, IOException {
		PageNode page = ctx.getPageMaker().getPageNode(l10n("title"), ctx);
		HTMLNode pageNode = page.outer;
		HTMLNode contentNode = page.content;
		
		contentNode.addChild(alerts.createSummary());
		
		HTMLNode contentBox = ctx.getPageMaker().getInfobox("infobox-information", l10n("title"), contentNode);
		
		contentBox.addChild("p", l10n("content1"));
		L10n.addL10nSubstitution(contentBox.addChild("p"), "InsertFreesiteToadlet.content2", new String[] { "jsite-http", "jsite-freenet", "jsite-freenet-version", "jsite-info", "/link"},
				new String[] { "<a href=\"/?_CHECKED_HTTP_=http://downloads.freenetproject.org/alpha/jSite/\">", 
				"<a href=\"/CHK@2gVK8i-oJ9bqmXOZfkRN1hqgveSUrOdzSxtkndMbLu8,OPKeK9ySG7RcKXadzNN4npe8KSDb9EbGXSiH1Me~6rQ,AAIC--8/jSite.jar\">", "0.6.2", "<a href=\"/SSK@ugb~uuscsidMI-Ze8laZe~o3BUIb3S50i25RIwDH99M,9T20t3xoG-dQfMO94LGOl9AxRTkaz~TykFY-voqaTQI,AQACAAE/FAFS-49/files/jsite.htm\">", "</a>" });
		contentBox.addChild("p", l10n("content3"));
		HTMLNode ul = contentBox.addChild("ul");
		HTMLNode li = ul.addChild("li");
		li.addChild("a", "href", "/SSK@940RYvj1-aowEHGsb5HeMTigq8gnV14pbKNsIvUO~-0,FdTbR3gIz21QNfDtnK~MiWgAf2kfwHe-cpyJXuLHdOE,AQACAAE/publish-1/", "Publish!");
		li.addChild("#", " - "+l10n("publishExplanation"));
		li = ul.addChild("li");
		li.addChild("a", "href", "/SSK@8r-uSRcJPkAr-3v3YJR16OCx~lyV2XOKsiG4MOQQBMM,P42IgNemestUdaI7T6z3Og6P-Hi7g9U~e37R3kWGVj8,AQACAAE/freesite-HOWTO-4/", "Freesite HOWTO");
		li.addChild("#", " - "+l10n("freesiteHowtoExplanation"));
		
		L10n.addL10nSubstitution(contentBox.addChild("p"), "InsertFreesiteToadlet.contentThingamablog", new String[] { "thingamablog", "thingamablog-freenet", "/link" },
				new String[] { "<a href=\"/?_CHECKED_HTTP_=http://downloads.freenetproject.org/alpha/thingamablog/thingamablog.zip\">", "<a href=\"/CHK@o8j9T2Ghc9cfKMLvv9aLrHbvW5XiAMEGwGDqH2UANTk,sVxLdxoNL-UAsvrlXRZtI5KyKlp0zv3Ysk4EcO627V0,AAIC--8/thingamablog.zip\">", "</a>" });
		
		this.writeHTMLReply(ctx, 200, "OK", pageNode.generate());
	}

	private static final String l10n(String string) {
		return L10n.getString("InsertFreesiteToadlet." + string);
	}

	@Override
	public String path() {
		return "/insertsite/";
	}

	@Override
	public String supportedMethods() {
		return "GET";
	}

}
