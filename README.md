# Library Management System

Note: Developed as per the requirement. 

Run the application using `gradle -i bootRun`

### RESTful endpoints and sample data

All services protected by using jwt authentication.
Get jwt token using authentication endpoint :

There are two users in the library system:

*dev user:* <br>
{<br>
"username" : "dev",<br>
"password" : "1234"<br>
}<br>

*admin user:*<br>
{
"username" : "admin",<br>
"password" : "0102"<br>
}<br>

## Login
Authenticates and generates JWT token
<br/>
HTTP Method : POST<br/>
Sample data : <br/>
curl --location 'http://localhost:8080/api/v1/library/login' \
--header 'content-type: application/json' \
--data '{
"username" : "admin",
"password" : "0102"
}'

## Add book 
Adds book to local store.<br/>
HTTP Method : POST<br/>
Sample data : <br/>
  <p>curl --location 'http://localhost:8080/api/v1/library/books' \
  --request POST
  --header 'content-type: application/json' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNjNmYTI5NmY5ZmQ0N2FmYjQzODE4YzVkMjM1Mjk2YyIsImlhdCI6MTczNDAwNzcyMiwic3ViIjoiYWRtaW4iLCJpc3MiOiJzZWNyZXRTZXJ2aWNlIiwiZXhwIjoxNzM0MDA5NTIyLCJ1c2VybmFtZSI6ImFkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdfQ.IRKI5h9LEh7mMbANi8pxziiVmTK0Iq_Shsob1t5LRH4'
  --data '{
  "isbn" : "123456",
  "title" : "Testing",
  "author" : "Tester",
  "publicationYear" : 1990
  }'

## Remove book
<p>Removes book from local store by using isbn<br/>
HTTP Method : DELETE<br/>
Sample data : <br>
curl 
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNjNmYTI5NmY5ZmQ0N2FmYjQzODE4YzVkMjM1Mjk2YyIsImlhdCI6MTczNDAwNzcyMiwic3ViIjoiYWRtaW4iLCJpc3MiOiJzZWNyZXRTZXJ2aWNlIiwiZXhwIjoxNzM0MDA5NTIyLCJ1c2VybmFtZSI6ImFkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdfQ.IRKI5h9LEh7mMbANi8pxziiVmTK0Iq_Shsob1t5LRH4'
--request DELETE --location  'http://localhost:8080/api/v1/library/books/123459' 

## FindBookByISBN
Returns a book by its ISBN <br/>
HTTP Method : GET<br/>
Sample data : <br>
curl
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNjNmYTI5NmY5ZmQ0N2FmYjQzODE4YzVkMjM1Mjk2YyIsImlhdCI6MTczNDAwNzcyMiwic3ViIjoiYWRtaW4iLCJpc3MiOiJzZWNyZXRTZXJ2aWNlIiwiZXhwIjoxNzM0MDA5NTIyLCJ1c2VybmFtZSI6ImFkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdfQ.IRKI5h9LEh7mMbANi8pxziiVmTK0Iq_Shsob1t5LRH4'
--request GET --location 'http://localhost:8080/api/v1/library/books/isbn/123456'

## FindBooksByAuthor 
<p> Returns a list of books by a given author
HTTP Method : GET<br/>
Sample data : <br>
curl 
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNjNmYTI5NmY5ZmQ0N2FmYjQzODE4YzVkMjM1Mjk2YyIsImlhdCI6MTczNDAwNzcyMiwic3ViIjoiYWRtaW4iLCJpc3MiOiJzZWNyZXRTZXJ2aWNlIiwiZXhwIjoxNzM0MDA5NTIyLCJ1c2VybmFtZSI6ImFkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdfQ.IRKI5h9LEh7mMbANi8pxziiVmTK0Iq_Shsob1t5LRH4'
--request --location 'http://localhost:8080/api/v1/library/books/author/Tester'

## BorrowBook 
Decreases the available copies of a book by 1 <br/>
HTTP Method : PATCH<br/>
Sample data : <br>
curl
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNjNmYTI5NmY5ZmQ0N2FmYjQzODE4YzVkMjM1Mjk2YyIsImlhdCI6MTczNDAwNzcyMiwic3ViIjoiYWRtaW4iLCJpc3MiOiJzZWNyZXRTZXJ2aWNlIiwiZXhwIjoxNzM0MDA5NTIyLCJ1c2VybmFtZSI6ImFkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdfQ.IRKI5h9LEh7mMbANi8pxziiVmTK0Iq_Shsob1t5LRH4'
--location --request PATCH 'http://localhost:8080/api/v1/library/books/borrow/123456'

## ReturnBook
Increases the available copies of a book by 1<br/>
HTTP Method : PATCH<br/>
Sample data : <br>
curl
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjNjNmYTI5NmY5ZmQ0N2FmYjQzODE4YzVkMjM1Mjk2YyIsImlhdCI6MTczNDAwNzcyMiwic3ViIjoiYWRtaW4iLCJpc3MiOiJzZWNyZXRTZXJ2aWNlIiwiZXhwIjoxNzM0MDA5NTIyLCJ1c2VybmFtZSI6ImFkbWluIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdfQ.IRKI5h9LEh7mMbANi8pxziiVmTK0Iq_Shsob1t5LRH4'
--request PATCH --location 'http://localhost:8080/api/v1/library/books/return/123456'

