package net.techcable.npclib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;

/**
 * Automatically loads npclib onto the classpath while avoiding conflicts
 */
class NPCLibLoader {

    public static final String GROUP_ID = "net.techcable";
    public static final String ARTIFACT_ID = "npclib";
    private static NPCLibClassLoader loader;
    private static final Object lock = new Object();

    public static NPCRegistry getNPCRegistry(org.bukkit.plugin.Plugin plugin) {
        waitTillLoaded();
        return NPCLib.getNPCRegistry(plugin);
    }

    public static NPCRegistry getNPCRegistry(String name, org.bukkit.plugin.Plugin plugin) {
        waitTillLoaded();
        return NPCLib.getNPCRegistry(name, plugin);
    }

    public static boolean isSupported() {
        waitTillLoaded();
        return NPCLib.isSupported();
    }

    public static boolean isNPC(Entity e) {
        waitTillLoaded();
        return NPCLib.isNPC(e);
    }

    private static long lastAlert = 0;
    private static final long ALERT_INTERVAL = 10000;

    private static void waitTillLoaded() {
        while (true) {
            synchronized (lock) {
                if (loader != null) return;
                if (lastAlert + ALERT_INTERVAL > System.currentTimeMillis()) {
                    Bukkit.getLogger().warning("[NPCLib] Waiting for NPCLib to load");
                    Bukkit.getLogger().warning("[NPCLib] Make sure to call NPCLibLoader.loadNPCLib()!");
                    lastAlert = System.currentTimeMillis();
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Loads npclib with the specified version
     * <p>
     * This method is thread safe but no other methods will block until this is loaded
     * </p>
     *
     * @param version the version of npclib to load
     *
     * @throws java.lang.IllegalStateException if already loaded
     * @throws java.io.IOException if an io error occured
     */
    public static void loadNPCLib(String version) throws IOException {
        synchronized (lock) {
            if (loader != null) throw new IllegalStateException("Already loaded");
            String versionUrl = "http://repo.techcable.net/content/groups/public/" + GROUP_ID.replace('.', '/') + "/" + ARTIFACT_ID + "/" + version;
            String baseUrl;
            if (version.endsWith("SNAPSHOT")) {
                MavenMetadata metadata = MavenMetadata.parse(versionUrl + '/' + "maven-metadata.xml");
                String withoutSnapshot = version.replace("-SNAPSHOT", "");
                baseUrl = versionUrl + "/" + ARTIFACT_ID + "-" + withoutSnapshot + "-" + metadata.timestamp + "-" + metadata.buildNumber;
            } else {
                baseUrl = versionUrl + "/" + ARTIFACT_ID + "-" + version;
            }
            URL jarUrl = new URL(baseUrl + ".jar");
            loader = new NPCLibClassLoader(jarUrl);
            try {
                loader.loadClass("net.techcable.npclib.NPCLib");
            } catch (ClassNotFoundException e) {
                throw new IOException("Unable to load NPCLib's main class", e);
            }
            lock.notifyAll();
        }
    }

    private static class NPCLibClassLoader extends URLClassLoader {

        public NPCLibClassLoader(URL url) {
            super(new URL[]{url});
        }
    }

    private static class MavenMetadata {

        private final String timestamp, buildNumber;

        private MavenMetadata(String timestamp, String buildNumber) {
            this.timestamp = timestamp;
            this.buildNumber = buildNumber;
        }

        public static MavenMetadata parse(String rawUrl) throws IOException {
            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                URLConnection connection = new URL(rawUrl).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));
                Document document = documentBuilder.parse(new InputSource(reader));
                NodeList versioningList = document.getElementsByTagName("versioning");
                if (versioningList.getLength() == 0) throw new IOException("Could not find versioning in maven metadata");
                Node versioning = versioningList.item(0);
                Node snapshot = null;
                for (int i = 0; i < versioning.getChildNodes().getLength(); i++) {
                    Node child = versioning.getChildNodes().item(i);
                    if ("snapshot".equals(child.getNodeName())) {
                        snapshot = child;
                        break;
                    }
                }
                if (snapshot == null) throw new IOException("Could not find versioning in maven metadata");
                String timestamp = null;
                String buildNumber = null;
                for (int i = 0; i < snapshot.getChildNodes().getLength(); i++) {
                    Node child = snapshot.getChildNodes().item(i);
                    if ("timestamp".equals(child.getNodeName())) {
                        timestamp = child.getFirstChild().getNodeValue();
                    }
                    if ("buildNumber".equals(child.getNodeName())) {
                        buildNumber = child.getFirstChild().getNodeValue();
                    }
                }
                if (timestamp == null) throw new IOException("Could not find timestamp in maven metadata");
                if (buildNumber == null) throw new IOException("Could not find build number in maven metadata");
                return new MavenMetadata(timestamp, buildNumber);
            } catch (ParserConfigurationException | SAXException ex) {
                throw new IOException("Unable to parse maven metadata", ex);
            }
        }
    }
}