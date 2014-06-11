Providing multiple .csv files separated by semicolon results in creating an excel sheet for each of this files.
```
java -jar exceller.jar test1.csv;test2.csv -h "Date;Name;RndStr;Rank" -columns "Dyyyy-mm-ddW7000;T;TW5000;N" -output "multiple_sheets.xlsx"
```