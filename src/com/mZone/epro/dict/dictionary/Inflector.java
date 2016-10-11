package com.mZone.epro.dict.dictionary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//==============Get Original word==========================
public class Inflector {
	
    protected static final Inflector INSTANCE = new Inflector();

    public static final Inflector getInstance() {
        return INSTANCE;
    }

    protected class Rule {

        protected final String expression;
        protected final Pattern expressionPattern;
        protected final String replacement;

        protected Rule( String expression,
                        String replacement ) {
            this.expression = expression;
            this.replacement = replacement != null ? replacement : "";
            this.expressionPattern = Pattern.compile(this.expression, Pattern.CASE_INSENSITIVE);
        }

        protected String apply( String input ) {
            Matcher matcher = this.expressionPattern.matcher(input);
            if (!matcher.find()) return null;
            return matcher.replaceAll(this.replacement);
        }

        @Override
        public int hashCode() {
            return expression.hashCode();
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj == this) return true;
            if (obj != null && obj.getClass() == this.getClass()) {
                final Rule that = (Rule)obj;
                if (this.expression.equalsIgnoreCase(that.expression)) return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return expression + ", " + replacement;
        }
    }

    private LinkedList<Rule> singulars = new LinkedList<Rule>();
    public Inflector() {
        initialize();
    }
	public String singularize( Object word ) {
        if (word == null) return null;
        String wordStr = word.toString().trim();
        if (wordStr.length() == 0) return wordStr;
        
        for (Rule rule : this.singulars) {
            String result = rule.apply(wordStr);
            if (result != null) return result;
        }
        return wordStr;
    }
    
    public List<String> getOriginal(String word) {
    	if (word == null) return null;
    	List<String> list = new ArrayList<String>();
        String wordStr = word.toString().trim();
        if (wordStr.length() == 0) return list;
        
        for (Rule rule : this.singulars) {
            String result = rule.apply(wordStr);
            if (result != null) list.add(result);
        }
        return list;
    }
    public void addSingularize( String rule,
            String replacement ) {
		final Rule singularizeRule = new Rule(rule, replacement);
		this.singulars.addFirst(singularizeRule);
	}

    /**
     * Completely remove all rules within this inflector.
     */
    public void clear() {

        this.singulars.clear();
    }
	 protected void initialize() {
	        Inflector inflect = this;
	       
	        inflect.addSingularize("s$", "");
	        inflect.addSingularize("(s|si|u)s$", "$1s"); // '-us' and '-ss' are already singular
	        inflect.addSingularize("(n)ews$", "$1ews");
	        inflect.addSingularize("([ti])a$", "$1um");
	        inflect.addSingularize("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
	        inflect.addSingularize("(^analy)ses$", "$1sis");
	        inflect.addSingularize("(^analy)sis$", "$1sis"); // already singular, but ends in 's'
	        inflect.addSingularize("([^f])ves$", "$1fe");
	        inflect.addSingularize("(hive)s$", "$1");
	        inflect.addSingularize("(tive)s$", "$1");
	        inflect.addSingularize("([lr])ves$", "$1f");
	        inflect.addSingularize("([^aeiouy]|qu)ies$", "$1y");
	        inflect.addSingularize("(s)eries$", "$1eries");
	        inflect.addSingularize("(m)ovies$", "$1ovie");
	        inflect.addSingularize("(x|ch|ss|sh)es$", "$1");
	        inflect.addSingularize("([m|l])ice$", "$1ouse");
	        inflect.addSingularize("(bus)es$", "$1");
	        inflect.addSingularize("(o)es$", "$1");
	        inflect.addSingularize("(shoe)s$", "$1");
	        inflect.addSingularize("(cris|ax|test)is$", "$1is"); // already singular, but ends in 's'
	        inflect.addSingularize("(cris|ax|test)es$", "$1is");
	        inflect.addSingularize("(octop|vir)i$", "$1us");
	        inflect.addSingularize("(octop|vir)us$", "$1us"); // already singular, but ends in 's'
	        inflect.addSingularize("(alias|status)es$", "$1");
	        inflect.addSingularize("(alias|status)$", "$1"); // already singular, but ends in 's'
	        inflect.addSingularize("^(ox)en", "$1");
	        inflect.addSingularize("(vert|ind)ices$", "$1ex");
	        inflect.addSingularize("(matr)ices$", "$1ix");
	        inflect.addSingularize("(quiz)zes$", "$1");
	        
	        inflect.addSingularize("ed$", "e");
	        inflect.addSingularize("([^i])(ed|er|est)$", "$1");
	        inflect.addSingularize("ing$", "");
	        inflect.addSingularize("ing$", "e");
	        inflect.addSingularize("ying$", "ie");
	        inflect.addSingularize("(ied|ier|iest|ily)$", "y");
	        inflect.addSingularize("([aeiou])(.)(\\2)(ing|ed|er|est)$", "$1$2");
	        
	        
	    }
}
