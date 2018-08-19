# Guardian News App

This is the fourth App I have created for Google Basics NanoDegree.

Its goal was to learn how to use API's and parsing JSON data while creating preference menu.

<a href="https://gfycat.com/gifs/detail/WideeyedUnselfishAuklet"><img src="https://thumbs.gfycat.com/WideeyedUnselfishAuklet-size_restricted.gif" title="made at imgflip.com"/></a>


The App includes it's own **QueryUtils** class that handles connecting the **App** to the **API** and parsing the received **Data** in **JSONobjects** and **JSONArrays**.

Its also includes a preference sitting layout that allows the user to choose the types of articles he wants to receive 

**This is how the App looks like:**

# The goal of this project was to learn:

-   Connecting to an API
-   Parsing the JSON response
-   Handling error cases gracefully
-   Updating information regularly
-   Using an AsyncTask
-   Doing network operations independent of the Activity lifecycle
-  mplement the Preference Fragment
-   Launch a Settings Activity from a menu in the Main Activity
-   Use Uri.Builder class to add query parameters to the URL
-   Update and display the Preference Summary
-   Using an AsyncTaskLoader

# I have created and used through this project:

 1. **Connecting to The  Guardian API**
 
 2. **QueryUtils class that include these methodes:**

|Method name  | Method description |
|--|--|
| fetchDataFromNewsURl | Its takes the String URL and fetch all the data from it. |
|createURL|create the url object|
|makeHTTPrequist|Its start the HTTP requist, open the connection, readFrom the stream.|
|readFromStream|` `read the stream and buffer it|
|extractFeatureFromJson|Parse the JSON and insert it into readable data|

 3. **Handle Exceptions**

 4. **AsyncTask**

 5. **AsyncTaskLoader**
    
 6. **CustomAdapter**

 7. **JSONObject - JSONArray**

 8. **Uri.Builder**

 9. **Handle Empty view**

 10. **Menu Items**

 11. **PreferenceFragment**

 12. **SharedPreferences**

 13. **Preference.OnPreferenceChangeListener**

 14. **TimeZone**

 15. **DateFormat**

 16. **SimpleDateFormat**

 17. **Date fromISO8601UTC**

 
