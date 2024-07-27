package gpjecc.blogspot.com;

import java.util.Iterator;

import javax.swing.plaf.metal.OceanTheme;

import org.lwjgl.util.input.ControllerAdapter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

/*bibliografia:
 * tiled map: http://www.gamefromscratch.com/post/2014/04/16/LibGDX-Tutorial-11-Tiled-Maps-Part-1-Simple-Orthogonal-Maps.aspx
 * sound track: https://downloads.khinsider.com/game-soundtracks/album/castlevania
 * sound efects: https://freesound.org/people/LittleRobotSoundFactory/packs/16681/
 */
public class SimpleGame extends ApplicationAdapter {
	boolean jogando;
	TelaInicial menu;
	Fase fase;
	
	@Override
	public void create() {
		jogando = false;
		menu = new TelaInicial();
		fase = new Fase();
	}

	@Override
	public void render() {
		if (Gdx.input.isKeyPressed(Keys.ENTER)) {
			jogando = !jogando;
		}
		if (jogando) {
			fase.render();
		} else {
			menu.render();
		}

	}

	@Override
	public void dispose() {

	}
}
//--------------------------------------classe movel--------------------------------------
abstract class Movel {
	protected SpriteBatch batch;
	protected Texture textura;
	protected Rectangle retangulo, srcRetangulo;
	protected boolean lado,caindo;
	

	Movel(String img) {
		batch = new SpriteBatch();
		textura = new Texture(img);
		retangulo = new Rectangle();
		srcRetangulo = new Rectangle();
		lado = true;
	}

	Texture getImage() {
		return textura;
	}

	Rectangle getCordenadas() {
		return retangulo;
	}

	void setCordenadas(int x, int y, int w, int h) {
		retangulo.x = x;
		retangulo.y = y;
		retangulo.width = w;
		retangulo.height = h;
	}

	public abstract void dispose();

	public abstract void desenha(int chao,float t);

}
//-----------------------------------------classe inimigo---------------------------------------------
class Inimigo extends Movel{

	Inimigo() {
		super("assets/imagens/male/Idle (1).png");
		setCordenadas(100, 20, 43, 64);
	}

	@Override
	public void dispose() {
		batch.dispose();
		textura.dispose();
	}
	public void mover(int chao,float t) {
		gravidade(chao);
	}

	void gravidade(int chao) {
		if (retangulo.y > chao) {
			retangulo.y -= 300 * Gdx.graphics.getDeltaTime();
			if (retangulo.y >= 100 + chao) {
				caindo = true;
			}
		} else if (retangulo.y <= chao) {
			caindo = false;
		}
	}
	@Override
	public void desenha(int chao, float t) {
		batch.begin();
		mover(chao,t);
		batch.draw(textura, retangulo.x, retangulo.y, retangulo.width, retangulo.height, (int)srcRetangulo.x, (int)srcRetangulo.y,
				(int)srcRetangulo.width, (int)srcRetangulo.height, lado, false);
		batch.end();
		
	}
	
}
//----------------------------------------------------classe player-----------------------------------
class Player extends Movel {
	private boolean ataque;
	private Sound chicote;
	private float vel;

	public Player() {
		super("assets/imagens/sprites.png");
		srcRetangulo.set(0, 0, 43, 64);
		chicote = Gdx.audio.newSound(Gdx.files.internal("assets/sons/347546__masgame__swoosh-sound-1.mp3"));
	}

	void controle(int chao,float t) {
		t*=100;
		System.out.println(t);
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			lado = false;
			vel = -10;
			anda((int)t);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			lado = true;
			vel = 10;
			anda((int)t);
		}
		if (Gdx.input.isKeyPressed(Keys.S) && retangulo.y <= 150 + chao && !caindo) {// aperte s para pular
			retangulo.y += 800 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			ataque = true;
			ataca((int)t);
		}

	}

	void anda(int t) {
		if (t % 25 == 0) {
			retangulo.x += vel ;
			srcRetangulo.set(0, 0, 43, 64);
		} else if (t % 15 == 0) {
			retangulo.x += vel ;
			srcRetangulo.set(43, 0, 43, 64);
		} else if (t % 5 == 0) {
			retangulo.x += vel ;
			srcRetangulo.set(86, 0, 43, 64);
		}
	}

	private void ataca(int t) {
		if (ataque) {
			if (t % 25 == 0) {
				srcRetangulo.set(0, 64, 75, 64);
			} else if (t % 15 == 0) {
				srcRetangulo.set(76, 64, 70, 64);
			} else if (t % 5 == 0) {
				srcRetangulo.set(146, 64, 95, 64);
				chicote.play();
				ataque = false;
			}
		}
	}

	public void mover(int chao,float t) {
		if (Gdx.graphics.getDeltaTime() % 100 == 0)
			srcRetangulo.set(0, 0, 43, 64);
		controle(chao,t);
		gravidade(chao);
	}

	void gravidade(int chao) {
		if (retangulo.y > chao) {
			retangulo.y -= 300 * Gdx.graphics.getDeltaTime();
			if (retangulo.y >= 100 + chao) {
				caindo = true;
			}
		} else if (retangulo.y <= chao) {
			caindo = false;
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		textura.dispose();
	}

	@Override
	public void desenha(int chao,float t) {
		batch.begin();
		mover(chao,t);
		retangulo.width = srcRetangulo.width;
		retangulo.height = srcRetangulo.height;
		batch.draw(textura, retangulo.x, retangulo.y, retangulo.width, retangulo.height, (int)srcRetangulo.x, (int)srcRetangulo.y,
				(int)srcRetangulo.width, (int)srcRetangulo.height, lado, false);
		batch.end();

	}
}
//---------------------------------------------classe texto-------------------------------------------------
class Texto {
	private BitmapFont font;
	private SpriteBatch batch;

	public Texto(int t) {
		font = new BitmapFont(new FileHandle("assets/font.fnt"), false);
		font.setColor(Color.YELLOW);
		font.setScale(t);
		batch = new SpriteBatch();
	}

	public void setTipo() {

	}

	public void setText(String s, int x, int y) {
		batch.begin();
		font.draw(batch, s, x, y);
		batch.end();
	}
}
//--------------------------------------------------classe tela inicial-----------------------------------------
class TelaInicial {
	private Texto titolo,instrucao;
	
	private Music musica;

	public TelaInicial() {
		titolo = new Texto(3);
		instrucao= new Texto(1);
		musica = Gdx.audio.newMusic(Gdx.files.internal("assets/sons/02. Vampire Killer (Courtyard).mp3"));
	}

	public void render() {
		titolo.setText("castlevania", 150, 400);
		instrucao.setText("press enter to start", 200, 200);
	}

	public void dispose() {
		musica.dispose();
	}
}
//-----------------------------------------------classe fase-----------------------------------------
class Fase {
	private Music musica;
	private OrthographicCamera camera;
	private Movel[] moveis;
	private int chao;
	private Tiled mapa;
	protected float tempo;
	
	public Fase() {
		moveis=new Movel[2];
		chao = 20;
		moveis[1] = new Player();
		moveis[2] = new Inimigo();
		tempo = Gdx.graphics.getDeltaTime();
		// mapa=new Tiled("assets/mapas/Mapa1.tmx");

		musica = Gdx.audio.newMusic(Gdx.files.internal("assets/sons/02. Vampire Killer (Courtyard).mp3"));

		musica.setLooping(true);
		musica.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		moveis[1].setCordenadas(50, chao, 64, 64);
	}

	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		tempo += Gdx.graphics.getDeltaTime();
		for (int i=0;i<2;i++)
			moveis[i].desenha(chao,tempo);
		camera.update();
	}

	public void dispose() {
		for (int i=0;i<2;i++)
			moveis[i].getImage().dispose();
		musica.dispose();
	}
}