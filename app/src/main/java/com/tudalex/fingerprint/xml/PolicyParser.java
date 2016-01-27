package com.tudalex.fingerprint.xml;

import android.util.Xml;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolicyParser {
    private static final String ns = null;
    private static final String TAG = "PARSER";


    public PolicySet parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            final PolicySet ps = readPolicySet(parser);
            Log.i("PARSER", ps.toString());
            return ps;
        } finally {
            in.close();
        }
    }

    public PolicySet readPolicySet(XmlPullParser parser) throws IOException, XmlPullParserException {
        PolicySet ps = new PolicySet();
        parser.require(XmlPullParser.START_TAG, ns, "policy-set");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals("policy")) {
                ps.policies.add(readPolicy(parser));
            } else if (name.equals("context-set")) {
                readContextSet(parser, ps);
            }
        }
        return ps;
    }

    private void readContextSet(XmlPullParser parser, PolicySet ps) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "context-set");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals("context")) {
                ps.contexts.add(readContext(parser));
            } else if (name.equals("context-provider")) {
                ps.contextProviders.add(readContextProvider(parser));
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "context-set");
    }

    private Context readContext(XmlPullParser parser) throws IOException, XmlPullParserException {
        Context ctx = new Context();
        parser.require(XmlPullParser.START_TAG, ns, "context");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals("rule")) {
                ctx.rules.add(readContextRule(parser));
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "context");
        return ctx;
    }

    private ContextRule readContextRule(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "rule");
        ContextRule cr = new ContextRule();
        cr.name = parser.getAttributeValue(ns, "attr");
        cr.value = Boolean.parseBoolean(parser.getAttributeValue(ns, "match"));
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "rule");
        return cr;
    }

    private ContextProvider readContextProvider(XmlPullParser parser) throws IOException, XmlPullParserException {
        ContextProvider cp = new ContextProvider();
        parser.require(XmlPullParser.START_TAG, ns, "context-provider");
        cp.id = parser.getAttributeValue(ns, "package-name");
        cp.signature = parser.getAttributeValue(ns, "signature");
        cp.uri = parser.getAttributeValue(ns, "source-url");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "context-provider");
        return cp;
    }



    public Policy readPolicy(XmlPullParser parser) throws IOException, XmlPullParserException {
        Policy p = new Policy();
        parser.require(XmlPullParser.START_TAG, ns, "policy");
        p.combine = parser.getAttributeValue(ns, "combine");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals("rule")) {
                p.rules.add(readRule(parser));
            } else if (name.equals("target")) {
                p.target = readTarget(parser);
            }
        }
        Log.i(TAG, p.toString());
        return p;
    }

    private Rule readRule(XmlPullParser parser) throws IOException, XmlPullParserException {
        Rule r = new Rule();
        parser.require(XmlPullParser.START_TAG, ns, "rule");
        r.effect = parser.getAttributeValue(ns, "effect");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals("condition")) {
                readCondition(parser, r);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "rule");
        Log.i("PARSER", r.toString());
        return r;
    }

    private void readCondition(XmlPullParser parser, Rule r) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "condition");
        while (parser.next() != XmlPullParser.END_TAG) {
            Log.i(TAG, "POS: " + parser.getPositionDescription());
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if (name.equals("resource-match")) {
                r.resource = parser.getAttributeValue(ns, "match");
                while (parser.next() != XmlPullParser.END_TAG)
                    ;
            } else if (name.equals("context-match")) {
                r.context = parser.getAttributeValue(ns, "match");
                while (parser.next() != XmlPullParser.END_TAG)
                    ;
            }
            Log.i(TAG, "Subject: " + name);
        }
        parser.require(XmlPullParser.END_TAG, ns, "condition");
    }

    public String readTarget(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "target");
        while (parser.nextToken() != XmlPullParser.START_TAG)
            ;
        Log.i("PARSER", "" + parser.getName());
        parser.require(XmlPullParser.START_TAG, ns, "subject");
        final String id =  parser.getAttributeValue(ns, "match");
        while (parser.next() != XmlPullParser.END_TAG)
            ;
        while (parser.next() != XmlPullParser.END_TAG)
            ;
        parser.require(XmlPullParser.END_TAG, ns, "target");
        return id;
    }
}
