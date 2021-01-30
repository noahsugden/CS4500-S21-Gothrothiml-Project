<h1>Traveller Module</h1>

<h2>Specification</h2>
<p>The following describes the specifications for the new Traveller module needed to provide route planning services for the new role-playing game. These controls will be necessary to create and navigate the town.</p>

<h3>Class TownNetwork</h3>
<p>
<h4>The TownNetwork Class will represent the entirety of the town network, maintaining towns, paths, and character positions.</h4>
<ul>
    <li>addTown(Town) -> void</li> - Add a new town to this TownNetwork.
    <li>addPath(Town, Town) -> void</li> - Add a path between the two given Towns.
    <li>placeCharacter(Character, Town) -> void</li> - Place the given Character at the given Town.
    <li>canReach(Character, Town) -> isReachable</li> - Determine if the given Character has a path to travel to the given Town.
</ul>
</p>  

<h4>Class Town</h4>
<p>
<h4>The Town module will represent a single town within the larger network, maintaining its own reachable set of towns.</h4>
<ul>
    <li>addPath(Town) -> void</li> - Add a path from this Town to the given Town.
    <li>canReach(Town) -> isReachable</li> - Determine if the given town is reachable from this Town.
</ul>
</p>
    
<h4>Class Character</h4>
<p>
<h4>The Character module represents a singular traveller with a unique name identifier.</h4>
<ul>
    <li>getName() -> Name</li> - Access the unique name identifier associated with this Character.
</ul>
</p>    

<p>
The described module should be implemented in Python 3.6.X and be thoroughly unit-tested.
</p>
