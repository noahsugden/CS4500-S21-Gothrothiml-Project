To: CS4500 Software Development Professors

From: Benjamin Lau, Bingqing Teng, Noah Sugden

Date: February 5, 2021

Subject: Feedback on Traveller Server Module Implementation


How well did the other team implement your specification? Did they follow it truthfully? If they deviated from it, was it well justified?

- The other team implemented our specification well.  They followed it truthfully to the best of their ability, and while they deviated from the specification somewhat, we believe their deviations are well justified because they provided an implementation memo explaining all of their decisions.  However, we believe this team could have made their choices more clear by including comments in the code.  
One of the changes they made was adding a name to the Person object.  We are not sure why they changed the Character class to Person, however, this change does not really affect any of the implementation, it just makes it somewhat confusing.  We agree with this because the JSON input would create a Character object with a string name.  The second change is that if a person has no path, the path is set to null, not -1.  We also agree with this change because, as stated in their memo, Java is a strongly typed language.  Lastly, they made Town a class instead of an interface so that Town objects can be initialized and can implement specific methods.  We agree with this change as well.

Were you or would you be able to integrate the received implementation with your client module from Task 3 of Warm-up 3? What was the actual or estimated effort required?

- We would be able to integrate the received implementation with our client module from Task 3 of Warm-up 3.  We don't believe this would take much effort becasue they followed our specification truthfully and only made minor changes.

Based on the artifact you received and the above two questions, how could you improve your specification to make it more amenable for implementation as you intended?

- We believe that the team's changes are sound.  Therefore, if we were to improve our specification to make it more amendable for implementation as intended, we would add a string name to the Character class, set a path to null if a Character is not on a path, and make Town a class rather than an interface.