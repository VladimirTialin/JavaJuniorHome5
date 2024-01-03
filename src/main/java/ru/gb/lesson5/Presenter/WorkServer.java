package ru.gb.lesson5.Presenter;

import ru.gb.lesson5.Model.Server;
import java.io.IOException;

public class WorkServer {
    public static void main(String[] args) throws IOException {
        new Server().start();
    }
}
