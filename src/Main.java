import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Main {

	private static List<String> cookiesHeader = null;

	public static void main(String[] args) throws IOException {

		Scanner scanner = new Scanner(System.in);//содали обьект сканер

		try { //МЕНЮ
			while (true) lebel: {
				System.out.println("1 - Authorization");
				System.out.println("2 - Registration");
				System.out.println("3 - MAIN CHAT");
				System.out.println("4 - CHAT ROOM");
				System.out.println("5 - PRIVATE MESSAGES");
				System.out.println("6 - logout");
				System.out.println("7 - Exit");

				String answer1 = scanner.nextLine(); //читаем сообщение
				String login = "";
				switch (answer1)  {
					case "1": //АВТОРИЗАЦИЯ
						System.out.println("Authorization form:");
						//форма с авторизацией, при успешной авторизации вернется логин, при не успешной null
						login = authorizationOrRegistrationOrCheck(scanner, "Authorization");
						break;

					case "2": //РЕГИСТРАЦИЯ
						System.out.println("Registration form:");
						//форма с регистрацией,
						authorizationOrRegistrationOrCheck(scanner, "Registration");
						break;

					case "3": //MAIN CHAT
						sendMessage(scanner, "MAIN-CHAT", false);
						break;

					case "4": //CHAT ROOM
						while(true)  {
							System.out.println("1 - Enter/Create chat-room");
							System.out.println("2 - Show chat room list:");
							System.out.println("3 - Exit");

							String answer3 = scanner.nextLine(); //читаем сообщение
							switch (answer3) {
								case "1": //ENTER/CREATE CHAT-ROM
									System.out.println("Enter chat-room name:");
									String roomName = scanner.nextLine(); //читаем сообщение
									roomName = roomName + "(CHAT-ROOM)"; //добавляем к названию метку, что это CHAT-ROOM
									if (!roomName.isEmpty()&&!roomName.contains(" ")) {
										//проверка существует ли комната
										String isExist = isExistRoom(roomName, false);
										if(isExist.equals("isExist")){ //если чат комната существует:
											System.out.println("Welcome too chat-room: " + roomName);
											sendMessage(scanner, roomName, false);
										} else if(isExist.equals("notExist")){ //если не существует
											System.out.println("Chat-room with name: \"" + roomName + "\" not exist!");
											System.out.println("Do you want create NEW Chat-rom? (y/n)");
											String answ = scanner.nextLine(); //читаем сообщение
											if ("y".equals(answ)) {
												String isCreated = isExistRoom(roomName, true);
												if(isCreated.equals("isCreated")) {
													System.out.println("Chat-room \""+roomName+"\" created");
													sendMessage(scanner, roomName, false);
												}
											}
										} else{ //если пользователь не залогинен
											System.out.println(isExist);
										}
									}
									else{
										System.out.println("Room name can not by empty, or contain \" \"");
									}
									break;
								case "2": //SHOW CHAT-ROOM LIST
									System.out.println("Chat-roms list:");
									showRoomsOrUsers("Rooms");
									break;
								case "3": //EXIT
									break lebel;
								default:
									System.out.println("Invalid Enter");
									break;
							}
						}

					case "5": //PRIVATE MESSAGE
						while (true) {
							System.out.println("1 - Sand message to User");
							System.out.println("2 - Show users list");
							System.out.println("3 - Exit");
							String answer4 = scanner.nextLine(); //читаем сообщение
							switch (answer4) {
								case "1": //SENG MASSEGE
									String answ = authorizationOrRegistrationOrCheck(scanner, "Check");
									if (answ != null) {
										if(!answ.equals(login)) {
											System.out.println("Private chat with " + answ);
											sendMessage(scanner, answ, true);
										}
										else{
											System.out.println("You can not send massage to your self!");
										}
									}
									break;
								case "2"://SHOW USER LIST
									System.out.println("User list:");
									showRoomsOrUsers("Users");
									break;
								case "3"://EXIT
									break lebel;
								default:
									System.out.println("Invalid Enter");
									break;
							}
						}
					case "6": //LOGOUT
						logout();
						break ;

					case "7": //ВЫХОД
						logout();
						return;

					default:
						System.out.println("Invalid enter");
						break ;
				}
			}
		} finally {
			scanner.close(); //закрываем сканер
		}
	}

	//АВТОРИЗАЦИЯ / РЕГИСТРАЦИЯ / проверка существования пользователя
	private static String authorizationOrRegistrationOrCheck(Scanner scanner, String aurorizOrRegOrCheck)throws IOException{
		String login = "";
		String passWord = "";
		// цикл с авторизацией/регистрацией который закончится только если регистрация/авторизация успешна и метод вернет login
		// либо если пользователь решил вернуться в меню метод вернет null
		while (true) {
			if(aurorizOrRegOrCheck.equals("Check")){
				System.out.println("Enter user login: "); //введите логин
				login = scanner.nextLine(); //читаем сообщение
				if (login.isEmpty()||login.contains(" ")){
					System.out.println("Login can not be empty or contain \" \"!");
					return null;
				}
			}
			else {
				System.out.println("Enter login: "); //введите логин
				login = scanner.nextLine(); //читаем сообщение
				System.out.println("Enter pssWord: "); //введите пароль
				passWord = scanner.nextLine(); //читаем сообщение
				//если пустой ввод сзару на переввод
				if (login.isEmpty() || passWord.isEmpty() || login.contains(" ") || passWord.contains(" ")) {
					System.out.println("Login and passWord can not be empty or contain \" \"!");
					return null;
				}
			}
			URL obj;
			if(aurorizOrRegOrCheck.equals("Authorization")) {
				obj = new URL("http://localhost:8080/autoriz?login=" + login + "&pas=" + passWord); //получаем обьект URL
			}
			else if(aurorizOrRegOrCheck.equals("Registration")){
				obj = new URL("http://localhost:8080/reg?login=" + login + "&pas=" + passWord); //получаем обьект URL
			}
			else{ //"Check"
				obj = new URL("http://localhost:8080/autoriz?login=" + login + "&pas="); //получаем обьект URL
			}

			HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //открываем URL соединение
			conn.setRequestMethod("POST"); //указываем тип запроса
			conn.setDoOutput(true);
			addCookie(conn);//добавляем Cookie (добавится только если проверка пользователя осуществляется)

			try(InputStream is = conn.getInputStream()) {// получаем входящий поток из url соединения

				int sz = is.available(); //спрашиваем сколько байт доступно для чтения
				String serverAnswer = null;
				if (sz > 0) {//если есть что читать
					byte[] buf = new byte[is.available()]; //создаем массив байт (на количество доступных для чтения байт)
					is.read(buf); //читаем байты в буфер
					serverAnswer = new String(buf); //преобразуем байт масисв в строку
					if("ok".equals(serverAnswer)){ //если сервер прислал подтвержение акаунта
						if(aurorizOrRegOrCheck.equals("Authorization")) {
							//если пришла куки - сохраняем
							Map<String, List<String>> headerFields = conn.getHeaderFields();
							cookiesHeader = headerFields.get("Set-Cookie");
							System.out.println("Authorization is successful!"); //выводим сообщение что авторизация успешна
						}
						if(aurorizOrRegOrCheck.equals("Registration")) {
							System.out.println("Registration is successful!"); //выводим сообщение что авторизация успешна
						}
						return login;
					}
					else {
						System.out.println(serverAnswer);
						if("You are logged already!".equals(serverAnswer)){
							return null;
						}
						System.out.println("1 - try again");
						System.out.println("2 - back to menu");
						String answer = scanner.nextLine(); //читаем сообщение
						switch (answer) {
							case "1":
								break;
							case "2":
								return null;
							default:
								System.out.println("Invalid enter");
								return null;
						}
					}
				}
			}
		}
	}
	//SEND MASSAGES (MAIN CHAT, CHAT ROOM, PRIVATE)
	//too - указываем кому пишем сообщеине и передаем в GET message поток для кого сообщения считывать
	//priv - флаг устанавливающий приватный диалог и GET message поток должен считывать только сообщения 2х пользователй друг другу
	private static void sendMessage(Scanner scanner, String too, boolean priv) throws IOException{

		GetThread th = new GetThread(too, priv); // создаем поток, который будет проверять не появились ли новые сообщения на сервере
		th.setDaemon(true); // устанавливаем его демоном, чтоб он завершился когда завершится программа
		th.start(); //запускаем его на выполнение

		System.out.println("Enter your message (empty enter for exit):");
		while (true) lebel: { //бесконечный цикд
			String text = scanner.nextLine(); //читаем сообщение
			if (text.isEmpty()) { //если сообщение пустое выйти
				System.out.println("Do you want exit from this chat?(y/n)");
				String answer = scanner.nextLine(); //читаем сообщение
				if("y".equals(answer)) {
					th.interrupt();//убиваем поток который обновляет сообщения из этого чата
					break;
				}
				else{
					System.out.println("Enter your message (empty enter for exit):");
					break lebel; //если пользователь не хочет выходить отпраляем его в начало цикла
				}
			}

			Message m = new Message(); // создаем обьект сообщение
			m.setText(text); //записываем введенный текст
			//m.setFrom(login); //от кого сообщение напишет сервер, исходя из Cookie
			m.setTo(too); //кому main-chat, chat-room или личные сообщения

			try {
				URL obj = new URL("http://localhost:8080/add"); //получаем обьект URL
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //открываем URL соединение
				conn.setRequestMethod("POST"); //указываем тип запроса
				conn.setDoOutput(true);//Используется для POST и PUT запросов. Если false, то это для использования GET запросов.
				addCookie(conn);//добавляем Cookie

				try(OutputStream os = conn.getOutputStream()) {
					String json = m.toJSON(); //получаем строку в JSON формате в которой записано наше сообщение
					os.write(json.getBytes()); //преобразуем строку в массив байтов и пишем в исходящий поток
					int res = conn.getResponseCode(); // спрашиваем код ошибки у сервера
					if (res != 200) { //если вернулся код ошибки не 200
						System.out.println("HTTP error: " + res); //выводим сообщение что данные не дошли на сервер и код
						if(res==401){
							System.out.println("Unauthorized request!");
						}
						return;
					}
				}
			} catch (IOException ex) {
				System.out.println("Error: " + ex.getMessage());
				return;
			}
		}
	}

	//LOGOUT
	private static void logout() throws IOException {
		//посылаем get запрос на сервер который установит пользователю офлайн
		URL obj = new URL("http://localhost:8080/autoriz"); //получаем обьект URL
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //открываем URL соединение
		addCookie(conn);
		try(InputStream is = conn.getInputStream()) {// получаем входящий поток из url соединения
			int sz = is.available(); //спрашиваем сколько байт доступно для чтения
			String serverAnswer = null;
			if (sz > 0) {//если есть что читать
				byte[] buf = new byte[is.available()]; //создаем массив байт (на количество доступных для чтения байт)
				is.read(buf); //читаем байты в буфер
				if(new String(buf).equals("logout")){
					cookiesHeader = null; //уничтожаем cookie
					System.out.println("You are logout!");
				}
			}
		}
	}

	//проверка сущесвует ли комната если create = false, создать новую комнату если create = true
	private static String isExistRoom(String roomName, boolean create) throws IOException {
		URL obj = new URL("http://localhost:8080/room?name=" + roomName + "&create=" + create); //получаем обьект URL
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //открываем URL соединение
		conn.setRequestMethod("POST"); //указываем тип запроса
		conn.setDoOutput(true);
		addCookie(conn);//добавляем Cookie
		try(InputStream is = conn.getInputStream()) {// получаем входящий поток из url соединения
			int sz = is.available(); //спрашиваем сколько байт доступно для чтения
			String serverAnswer = null;
			if (sz > 0) {//если есть что читать
				byte[] buf = new byte[is.available()]; //создаем массив байт (на количество доступных для чтения байт)
				is.read(buf); //читаем байты в буфер
				serverAnswer = new String(buf); //преобразуем байт масисв в строку
				return serverAnswer;
			}
		}
		return "Server error!";
	}

	//показать список чат-комнат или пользователей
	private static void showRoomsOrUsers(String roomsOrUsers)throws IOException{
		URL obj;
		if(roomsOrUsers.equals("Rooms")) {
			obj = new URL("http://localhost:8080/room"); //получаем обьект URL
		}
		else{
			obj = new URL("http://localhost:8080/reg"); //получаем обьект URL
		}
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //открываем URL соединение
		addCookie(conn);//добавляем Cookie

		try(InputStream is = conn.getInputStream()) {// получаем входящий поток из url соединения
			int sz = is.available(); //спрашиваем сколько байт доступно для чтения
			String serverAnswer = null;
			if (sz > 0) {//если есть что читать
				byte[] buf = new byte[is.available()]; //создаем массив байт (на количество доступных для чтения байт)
				is.read(buf); //читаем байты в буфер
				serverAnswer = new String(buf); //преобразуем байт масисв в строку
				System.out.println(serverAnswer);
			}
		}
	}

	public static void addCookie(HttpURLConnection conn){
		if(cookiesHeader != null && cookiesHeader.size()>0) {
			conn.setRequestProperty("Cookie", String.join (";", cookiesHeader));
		}
	}
}
