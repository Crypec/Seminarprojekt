package util;

import java.util.*;
import java.lang.*;

import com.google.gson.*;

public class Report {


    // TODO(Simon): rename to somehting more generic, we use the same excpetion in the typechecking stage as well
    public class ParseError extends RuntimeException{}

    // if we encounter a fatal error while parsing we set this field to not execute corrupted code
    public static boolean hadErr = false;

    private boolean isFatal = false; 

    private String errType;
    private String errMsg;
    private List<String> examples;
    private Token token;

    // url to resources where the user can learn more about his error
    private String url;

    public Report(String errType, String errMsg, List<String> examples,
		  Token token, String url, boolean isFatal) {
	
	this.errType = errType;
	this.errMsg = errMsg;
	this.examples = examples;
	this.token = token;
	this.url = url;
	this.isFatal = isFatal;
    }

    public static class Builder {

	private boolean isFatal = false;
	
	private String errType;
	private String errMsg;
	private List<String> examples;
	private Token token;
	private String url;

	public Builder() { this.examples = new ArrayList<String>(); }

	public Builder errWasFatal() {
	    this.isFatal = true;
	    Report.hadErr = true;
	    return this;
	}

	public Builder setErrorType(String errType) {
	    this.errType = errType;
	    return this;
	}

	public Builder withErrorMsg(String errMsg) {
	    this.errMsg = errMsg;
	    return this;
	}

	public Builder addExample(String example) {
	    this.examples.add(example);
	    return this;
	}

	public Builder atToken(Token token) {
	    this.token = token;
	    return this;
	}

	public Builder url(String url) {
	    this.url = url;
	    return this;
	}

	public Report create() {
	    return new Report(this.errType, this.errMsg, this.examples, this.token,
			      this.url, isFatal);
	}
    }

    public void setToken(Token t) {
	this.token = t;
    }
    
    
    // if we encounter an error during the parsing stage we can use the following exception to restore state and continue parsing till the end
    // this means we can try to detect all errors and not just a single error every time the user tries to compile a programm
    public void sync() throws ParseError {
	if (this.isFatal) throw new ParseError();
    }

    // TODO(Simon): Implement nice pretty prining of error/warning messages
    @Override
    public String toString() {
	return new GsonBuilder()
	    .setPrettyPrinting()
	    .serializeNulls()
	    .create()
	    .toJson(this);
    }
}
	// this means we can try to detect all errors and not just a single error every time the user tries to compile a programm
	// TODO(Simon): Implement nice pretty prining of error/warning messages
	@Override
	public String toString() {
		return "----------------------------------------------------!ACHTUNG!----------------------------------------------------"
				+ "\n"
				+ this.errType + "\n"//Bsp. falschen Typ
				+ this.errMsg + "\n"//
				+ this.token.getMeta().getFilename() + "\n"//Dateiname
				+ "schau mal hier in der Linie" + this.token.getMeta().getLine() + ", " + "\n"//Linie des fehlers
				+ "bei" + readLineFromFile()+"\n"
				+ "wie gesagt glauben wir, dass du hier" + getMistake() + "benutzt hast!" + "\n"// e.g. Beschreibung von fehler
				+ "probier es an der Stelle doch mal mit" + getSolution() + "\n" // unser Verbesserungsvorschlag
				+ "Hier gilt nämlich: " + getRule() + "und hier ist die URL" + this.url + "zum Arbeitsblatt, damit du auch verstehst wieso das so sein muss :) \n"
				+ "-----------------------------------------------------------------------------------------------------------------";
	}			//getRule gibt die Regel, passen zum errTyp an
	// ausgeben und Rot markiern was falsch +
	// wie liest man eine Datei (mettdaten filename)

	// TODO(Torben): errTypes vereinen und hinzfügen getSolution nicht vergessen!!!
	private String getMistake(String errType) {
		String i = this.errType;

		switch(i) {

			case 'type':
				return " einen falschen Typ ";
			break;
			case 'variable':
				return " eine falsche oder schon benutzte Variable ";
			break;
			case 'spellMistake':
				return " eine falsche Rechtschreibung ";
			break;
			case 'signature':
				return "eine falsche From der Signatur ";

		}
	}

	//TODO(Torben): Fun mit oberen erweitern und vereinen
	private String getSolution(String errType) {

		String j = this.errType;

		switch (j) {
			case 'type':
				return "einem anderen Typ, wie wäre es mit dem Typ " + switchTyp();//switchTyp soll anzeigen ob Zahl zu Text oder Text zu Zahl werden sollte
			break;
			case 'variable'
				return "einer anderen Varibale, du hast benutzte Varible nämlich schon genutzt!";//Opration hinzufügen die freie Variable anzeigt
			break;
			case 'spellMistake'
				return "mit dem ganz genauen Durchlesen des Wortes, du findest den fehler bestimmt;)";
			break;
			case 'signature'
				return "";//TODO(Torben): einfügen was an der Signatur falsch sein kann
		}
	}

	private String switchType(){
		//Variable(e.g.type) + Initialisierung zu Text oder Zahl oder Boolean, da müssen wir schauen wie wir das machen!
		if (type.equals("text")){

			return "Zahl";
			break;

		}else if(type.equals("zahl")){

			return "Text";
			break;
		}

		private String getRule(String errType){
			r = this.errType;

			switch(r){

				case 'type':
					return "";// wen Zahl benutzt wurde bei Buchstabe und wenn Text bei Zahl benutzt wurde ?!?;
				break;
				case 'variable'//
					return "Du kannst eine Variable die du in einer Klasse definiert hast nur einmal in der Klasse benutzten, es sei den es ist ein Feld";//TODO(Torben)incldue Arry case
				break;
				case 'spellMistake'
					return "Auch beim Programmieren gilt die DEUTSCHE Rechtschreibung";
				break;
				case 'signature'
					return"";//TODO(Torben):Signatur fehler einbeziehen
			}
		}


		public String readLineFromFile() {//TODO: (Torben) feddisch schreiben!!!

			FileReader fr = new FileReader(this.token.getMeta().getFilename());
			BufferedReader br = new BufferedReader(fr);
			return br.readLine();//komplette zeile des Fehlers

		}
		public void sync() throws ParseError {
			if (this.isFatal) throw new ParseError();
		}

		// TODO(Simon):
		public String toString() {
			return new GsonBuilder()
					.setPrettyPrinting()
					.serializeNulls()
					.create()
					.toJson(this);
		}
	}

