# LetsGo
This applications shows a limited amount of locations and shows user location. This is shown in the MapView and the ListView

The application was very interesting, I would say I spent about an hour working with adding user location since the API for MapBox 
was provided to me. There is an issue, that I am having a challenge with and that is when the user accepts the runtime permission
for location, their location isn't shown yet. It is shown when I restart the application however, if I had a bit more time I could've
fixed this.

It took me about half a day to implement LiveData, ViewModel, Room, and SharedPreferences as data persistency to the app.
It was a lot of fun to implement this and persist data within the app, I use SharedPreferences to tell me if the network call 
worked and if it did using LiveData and Room I save it into the internal database. When the application is started again, it checks
the sharedprefences to see if we used the network call, if true then ViewModel, Room and LiveData will come in and access the database
to retrieve the list of objects we saved earlier.

It took me about a full day to implement Volley and the core of the application. I really enjoy using volley due to its simplicity.
I used RecyclerView, Adapter, Viewholders. My plan was to make an abstract viewholder and other future viewholders would extend
from that abstract class so we could reuse the same adapter for future features!

I spent about 1-2 days figuring out the MapBox API in general especially since it was deprecated, however the example code that was
provided by the document proved to be very helpful so adding styles to the map, instead of things like markers was pretty neat!
