The threads used to be held in a HashMap, thus, when waiting on them to complete for `join()` you would wait in a random order. Now that has been changed to a arraylist. So now you wait on the oldest thread first, newest last.

Now program is in V2