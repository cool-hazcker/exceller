When -h and -H keys are both present, then the first string in CSV file is considered to contain column header names, We read this string but replace the header names with the ones provided with -h key.
```
java -jar exceller.jar test.csv -H -h "Date;Name;RndStr;Married;Height;Rank" -columns "Dyyyy-mm-ddW7000;T;TW5000"
```