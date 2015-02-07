NPCLib [![Build Status](http://ci.techcable.net/buildStatus/icon?job=NPCLib)](http://ci.techcable.net/job/NPCLib/)
======
Easy spawning of npcs

##Supported Versions
- 1.8
- fake 1.8
- 1.7.10
- 1.7.9
- 1.7.8
- Citizens
  - Allows spawning on any version of minecraft

##Maven

###Repository
````xml
<repository>
  <id>techcable-repo</id>
  <url>http://repo.techcable.net/content/groups/public/</url>
</repository>
````
###Dependency
````xml
<repository>
  <groupId>techcable.minecraft</groupId>
  <artifactId>npclib-base</artifactId>
  <version>LATEST</version>
</repository>
````

##Usage
[**Javadocs**](http://ci.techcable.net/job/NPCLib/javadoc/)

Example:
````java
public void spawn() {
    if (!NPCLib.isSupported()) return;
    NPCRegistry registry = NPCLib.getNPCRegistry("awesome-registry", getPlugin());
    NPC npc = registry.createNPC(EntityType.PLAYER, "jack");
    npc.setName("greeter");
    npc.setProtected(true); //Makes invincible
    npc.setSkin(lookupId("Techcable")); //Gives the npc my troll skin
    npc.spawn(getSpawn()); //Spawns at server spawn
}
````
