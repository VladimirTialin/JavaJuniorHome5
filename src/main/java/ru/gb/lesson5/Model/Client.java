package ru.gb.lesson5.Model;
import ru.gb.lesson5.Model.Interface.SettingClient;
import ru.gb.lesson5.Model.Interface.Stream;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client implements Stream, SettingClient {
  public  void start(){
    threadRead();
    threadWrite();
  }
  @Override
  public void threadRead() {
    new Thread(() -> {
      try (Scanner input = new Scanner(connected().getInputStream())) {
        while (true) {
          try{
            System.out.println(input.nextLine());
          }catch (Exception e)
          {
            System.out.println("Отключен от сервера");
            break;
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).start();
  }
  @Override
  public void threadWrite() {
    new Thread(() -> {
      try (PrintWriter output = new PrintWriter(connected().getOutputStream(),true)){
        Scanner consoleScanner = new Scanner(System.in);
        while (true) {
          String consoleInput = consoleScanner.nextLine();
          output.println(consoleInput);
          if (Objects.equals("q", consoleInput)) {
            output.close();
            connected().close();
            break;
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).start();
  }
  private Socket connected() throws IOException {
    return new Socket(ip,port);
  }
}


