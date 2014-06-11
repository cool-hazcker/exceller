The -il key means that we should apply the last pattern from the pattern string ("N" in our case) to the rest of the columns (Height and Rank in our case).
```
java -jar exceller.jar test.csv -h "Date;Name;RndStr;Married;Height;Rank" -columns "Dyyyy-mm-ddW7000;T;TW5000;B;N" -il -output "awesome_table.xlsx"
```