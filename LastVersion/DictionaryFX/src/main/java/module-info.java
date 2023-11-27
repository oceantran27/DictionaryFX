module uet.cs.dictionaryfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires freetts;

    opens uet.cs.dictionaryfx.dictionary.model to javafx.fxml;
    //opens uet.cs.dictionaryfx.dictionary.gui to javafx.fxml;
    opens uet.cs.dictionaryfx.game.gui to javafx.fxml;
    opens uet.cs.dictionaryfx.game.model to javafx.fxml;

    exports uet.cs.dictionaryfx.dictionary.model;
    //exports uet.cs.dictionaryfx.dictionary.gui;
    exports uet.cs.dictionaryfx.game.model;
    exports uet.cs.dictionaryfx.game.gui;
}

