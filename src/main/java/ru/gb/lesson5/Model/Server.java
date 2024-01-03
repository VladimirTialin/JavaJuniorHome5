package ru.gb.lesson5.Model;

import lombok.Getter;
import lombok.Setter;
import ru.gb.lesson5.Model.Interface.SettingServer;
import ru.gb.lesson5.Model.Interface.Stream;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Server implements SettingServer {

  private long clientId;
  private  Map<Long,SocketWrapper> clients=new HashMap<>();
  private long clientIdCounter = 1L;
  public Server() {
  }
  public void start() throws IOException{
    try (ServerSocket server = new ServerSocket(8081)) {
      System.out.println("Сервер запущен на порту " +port);
      while (true) {
        final Socket client = server.accept();
        clientId = clientIdCounter++;
        SocketWrapper wrapper = new SocketWrapper(clientId, client);
        System.out.println("Подключился новый клиент[" + wrapper + "]");
        clients.put(clientId, wrapper);
        theadServer(wrapper);
      }
    }
  }
  private void theadServer(SocketWrapper wrapper) {
    new Thread(() -> {
      try (Scanner input = wrapper.getInput(); PrintWriter output = wrapper.getOutput()) {
        output.println("Подключение успешно. Список всех клиентов: " + clients);
        while (true) {
          String clientInput = input.nextLine();
          if (Objects.equals("q", clientInput)) {
            // todo разослать это сообщение всем остальным клиентам
            clients.remove(clientId);
            clients.values().forEach(it -> it.getOutput().println("Клиент[" + clientId + "] отключился"));
            break;
          }
          messages(clientInput);
        }
      }
    }).start();
  }
  private boolean chatKey(String msg){
    Character symbol= msg.charAt(0);
    return symbol.equals('@');
  }
  private void messages(String msg){
    if(chatKey(msg)) {
      long destinationId = Long.parseLong(msg.substring(1, 2));
      SocketWrapper destination = clients.get(destinationId);
      destination.getOutput().println(msg);
    }else{
      clients.values()
              .forEach(socket -> socket
                      .getOutput().println(msg));
    }
  }
}