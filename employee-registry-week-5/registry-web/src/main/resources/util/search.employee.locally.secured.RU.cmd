curl ^
  -H "x-api-key: 218d3f55-pjiq-2738-vo3d-ed2fcd586b65" ^
  -v localhost:8080/registry/employee?q=%%D0%%B7%%D0%%B0

@REM first URL encode cyrillic query string
@REM then for Windows platform escape % with another % character (e.g. %D0%B0 becomes %%D0%%B0)
