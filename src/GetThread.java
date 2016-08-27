import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//параллельный коток, который будет проверять не появились ли новые сообщения на сервере

public class GetThread extends Thread { //наследуемся от потока

    private String login; //логин пользователя(для мониторинга личных сообщений)
    private String too; //в какой чат адресовано сообщение
    boolean priv;//флаг приватные сообщения
    private int n; //счетчик уже загруженных сообщений

    public GetThread(String login, String too, boolean priv){
        this.login = login;
        this.too = too;
        this.priv = priv;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    @Override
    public void run() { //метод который будет выполнятся в параллельном потоке
        try {
            while (!isInterrupted()) { //повторять пока не прервали

                try { //чуток спим, чтоб не так напрягался процессор
                    currentThread().sleep(10);
                }
                catch (InterruptedException e){
                    return; //если во время сна прервали, выходим из метода
                }
                // передаем параметр from = сколько сообщений уже прочитано,
                // и too - кому адресовано сообщение(main-chat, chat-room или личное)

                URL url = new URL("http://localhost:8080/get?from="+n+"&login="+login+"&too="+too+"&priv="+priv); //URL ссылка на сервлет /get с параметром n -
                // колличество уже проитанных сообщений
                HttpURLConnection http = (HttpURLConnection) url.openConnection(); //открываем url соединение

                try(InputStream is = http.getInputStream()) {// получаем входящий поток из url соединения
                    int sz = is.available(); //спрашиваем сколько байт доступно для чтения
                    if (sz > 0) {//если есть что читать
                        byte[] buf = new byte[is.available()]; //создаем массив байт (на количество доступных для чтения байт)
                        is.read(buf); //читаем байты в буфер
                        Gson gson = new GsonBuilder().create(); //создаем обьект gson
                        Message[] list = gson.fromJson(new String(buf), Message[].class); //парсим данные из byte массива
                        //в котором записан массив сообщений в JSON формате
                        for (Message m : list) { //выводим все сообщения на экран, итерируем счетчик
                            n = m.getCounter()+1;
                            System.out.println(m);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
}
