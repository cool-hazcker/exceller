Providing a hyperlink column pattern ("H"). Perform a slice operation providing the -n parameter. Only -n columns from the csv source are getting converted.
```
java -jar exceller.jar test.csv -h "Date;Link;Name"  -columns "Dyyyy-mm-ddW7000;H;TW5000" -n 3
```