curl ^
  -H "x-api-key: e03fe1ee-12f1-43ee-9bc8-8d12ff95fc93" ^
  -v localhost:8080/registry/employee?q=%%D0%%B7%%D0%%B0

@REM first URL encode cyrillic query string
@REM then for Windows platform escapre % with another % character (e.g. %D0%B0 becomes %%D0%%B0)
