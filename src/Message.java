import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

//обьект сообщение
public class Message implements Serializable { //серализация??????

	private static final long serialVersionUID = 1L; //версия

	private Date date = new Date(); //дата создания
	private String from; //от кого
	private String to;//кому
	private String text;//сообщение
	
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

	//отправить сообщение принимаем ссылку куда отправить, возвращает код ошибки сервера
	public int send(String url) throws IOException {
		URL obj = new URL(url); //получаем обьект URL
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //открываем URL соединение
		
		conn.setRequestMethod("POST"); //указываем тип запроса
		conn.setDoOutput(true);
		//setDoOutput(true) Используется для POST и PUT запросов. Если false, то это для использования GET запросов.
	
		OutputStream os = conn.getOutputStream(); //получаем исходящий поток для URL соединения
		try {
			String json = this.toJSON(); //получаем строку в JSON формате в которой записано наше сообщение
			os.write(json.getBytes()); //преобразуем строку в массив байтов и пишем в исходящий поток
			
			return conn.getResponseCode(); // спрашиваем код ошибки у сервера, и возвращаем как результат работы метода
		} finally {
			os.close(); //закрываем исходящий поток
		}
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
}
