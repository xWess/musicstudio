module asm.org.MusicStudio {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires static lombok;
    requires jakarta.mail;
    requires bytes;
    requires bcrypt;
    requires org.postgresql.jdbc;
    requires javafx.base;
    requires javafx.graphics;

    opens asm.org.MusicStudio to javafx.fxml;
    opens asm.org.MusicStudio.controllers to javafx.fxml;
    opens asm.org.MusicStudio.entity to javafx.base;

    exports asm.org.MusicStudio;
    exports asm.org.MusicStudio.controllers;
    exports asm.org.MusicStudio.entity;
    exports asm.org.MusicStudio.services;
    exports asm.org.MusicStudio.util;
}