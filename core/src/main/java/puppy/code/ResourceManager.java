package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager implements Disposable {
    private static ResourceManager instance;
    private Map<String, Texture> textures;
    private Map<String, Sound> sounds;
    private Map<String, Music> music;
    
    // FALLBACK EN CASO DE NO EXISTIR TEXTURA
    private Texture fallbackTexture;
    
    private ResourceManager() {
        textures = new HashMap<>();
        sounds = new HashMap<>();
        music = new HashMap<>();
        createFallbackTexture();
        loadDefaultResources();
    }
    
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    // CREAR FALLBACK TEXTURA SIMPLE
    private void createFallbackTexture() {
        try {
            // EN CASO DE INTENTAR CARGAR UNA TEXTURA QUE SABEMOS QUE EXISTE
            fallbackTexture = new Texture(Gdx.files.internal("Rocket2.png"));
            Gdx.app.log("ResourceManager", "Fallback texture created");
        } catch (Exception e) {
            Gdx.app.error("ResourceManager", "Could not create fallback texture", e);
        }
    }
    
    private void loadDefaultResources() {
        // PRECARGAR RECURSOS
        loadTexture("MainShip3.png");
        loadTexture("Rocket2.png");
        loadTexture("aGreyMedium4.png");
        loadTexture("escudo.png");
        loadTexture("oneup.png");
        
        // PRECARGAR FONDO HARDCORE
        loadTexture("spacehardcore.png");
        
        loadSound("explosion.ogg");
        loadSound("hurt.ogg");
        loadSound("pop-sound.mp3");
        
        // MÚSICAS
        loadMusic("piano-loops.wav"); // MUSICA MODO NORMAL
        
        // PRECARGAR MUSICA (INTENTAR VARIOS FORMATOS)
        loadMusic("hardcoremusic.mp3");
        loadMusic("hardcoremusic.ogg"); 
        loadMusic("hardcoremusic.wav");
    }
    
    public Texture getTexture(String fileName) {
        if (!textures.containsKey(fileName)) {
           
            if (!loadTexture(fileName)) {
               
                Gdx.app.error("ResourceManager", "Using fallback for: " + fileName);
                return fallbackTexture;
            }
        }
        return textures.get(fileName);
    }
    
    public Sound getSound(String fileName) {
        if (!sounds.containsKey(fileName)) {
            loadSound(fileName);
        }
        return sounds.get(fileName);
    }
    
    public Music getMusic(String fileName) {
        if (!music.containsKey(fileName)) {
            loadMusic(fileName);
        }
        return music.get(fileName);
    }
    
    // VERIFICAR SI LA TEXTURA EXISTE
    public boolean hasTexture(String fileName) {
        if (textures.containsKey(fileName)) {
            return true;
        }
        // INTENTAR CARGARLA
        return loadTexture(fileName);
    }
    
    // VERIFICAR SI LA MUSICA EXISTE
    public boolean hasMusic(String fileName) {
        if (music.containsKey(fileName)) {
            return true;
        }
        // INTENTAR CARGARLA
        return loadMusic(fileName);
    }
    
    // VERIFICAR SI EXISTE EL SONIDO
    public boolean hasSound(String fileName) {
        if (sounds.containsKey(fileName)) {
            return true;
        }
        // INTENTAR CARGAR
        return loadSound(fileName);
    }
    
    // RETORNAR TRUE SU CARGA, FALSE SI FALLA
    private boolean loadTexture(String fileName) {
        try {
            Texture texture = new Texture(Gdx.files.internal(fileName));
            textures.put(fileName, texture);
            Gdx.app.log("ResourceManager", "Texture loaded: " + fileName);
            return true;
        } catch (Exception e) {
            Gdx.app.error("ResourceManager", "Error loading texture: " + fileName, e);
          
            return false;
        }
    }
    
    private boolean loadSound(String fileName) {
        try {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(fileName));
            sounds.put(fileName, sound);
            Gdx.app.log("ResourceManager", "Sound loaded: " + fileName);
            return true;
        } catch (Exception e) {
            Gdx.app.error("ResourceManager", "Error loading sound: " + fileName, e);
            return false;
        }
    }
    
    private boolean loadMusic(String fileName) {
        try {
            Music musicObj = Gdx.audio.newMusic(Gdx.files.internal(fileName));
            music.put(fileName, musicObj);
            Gdx.app.log("ResourceManager", "Music loaded: " + fileName);
            return true;
        } catch (Exception e) {
            Gdx.app.error("ResourceManager", "Error loading music: " + fileName, e);
            return false;
        }
    }
    
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        for (Music musicObj : music.values()) {
            musicObj.dispose();
        }
        if (fallbackTexture != null) {
            fallbackTexture.dispose();
        }
        textures.clear();
        sounds.clear();
        music.clear();
        Gdx.app.log("ResourceManager", "All resources disposed");
    }
    
    public int getTextureCount() {
        return textures.size();
    }
    
    public int getSoundCount() {
        return sounds.size();
    }
    
    public int getMusicCount() {
        return music.size();
    }
}