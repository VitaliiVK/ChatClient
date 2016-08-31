import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

//обьект сообщение
public class Message implements Serializable { //серализация??????

	private static final long serialVersionUID = 1L; //версия

	private Date date = new Date(); //дата создания
	private String from; //от кого
	private String to;//кому
	private String text;//сообщение
	private int counter;
	
	public String toJSON() { //преобразователь в формат JSON
		Gson gson = new GsonBuilder().create();
		return gson.toJson(this);
	}
	
	public static Message fromJSON(String s) { // преобразователь из JSON
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(s, Message.class);
	}

	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss");
		return new StringBuilder()
				.append("[")
				.append("From: ")
				.append(from)
				.append(", To: ")
				.append(to)
				.append(", ")
				.append(dateFormat.format(date))
				.append("] ")
				.append(text).toString();
	}


	//геттеры и сеттеры
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
