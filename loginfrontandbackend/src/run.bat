cd /d E:\javapbl\examprep\src

javac -cp ".;../lib/mysql-connector-j-9.6.0.jar" --module-path "E:/javafxproject/javafx-sdk-26/lib" --add-modules javafx.controls,javafx.fxml *.java

java -cp ".;../lib/mysql-connector-j-9.6.0.jar" --module-path "E:/javafxproject/javafx-sdk-26/lib" --add-modules javafx.controls,javafx.fxml LoginPage

pause