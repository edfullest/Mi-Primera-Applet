/**
 * JuegoApplet
 *
 * Anima un Link y con las flechas se puede mover
 *
 * @author Antonio Mejorado
 * @version 2.0 2015/1/15
 */
 
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import javax.swing.ImageIcon;

public class JuegoApplet extends Applet implements Runnable, KeyListener {
    // Se declaran las variables y objetos
    // direccion en la que se mueve el Link
    // 1-arriba,2-abajo,3-izquierda y 4-derecha
    private int iDireccion;                    
    private AudioClip aucSonidoLink;     // Objeto AudioClip sonido Elefante
    private Animal aniLink;         // Objeto de la clase Elefante
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    Image imaImagenLinkDer; //Link Derecho
    Image imaImagenLinkDerGolpe;  
    Image imaImagenLinkIzq; //Link Izquierdo
    Image imaImagenLinkIzqGolpe;
    Image imaImagenLinkArriba; //Link Arriba
    Image imaImagenLinkArribaGolpe;
    Image imaImagenLinkAbajo; //Link Abajo
    Image imaImagenLinkAbajoGolpe;
    int VelY;
    int VelX;
    boolean bolGolpeDer;
    boolean bolGolpeIzq;
    boolean bolGolpeArriba;
    boolean bolGolpeAbajo;
    int tiempoCambioSprite;
    /** 
     * init
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inicializan las variables o se crean los objetos
     * a usarse en el <code>Applet</code> y se definen funcionalidades.
     * 
     */
    public void init() {
        // hago el applet de un tamaño 600,600
        setSize(600,600);
        
        //Inicializo los booleanos en falso
        bolGolpeDer=false;
        bolGolpeIzq=false;
        bolGolpeArriba=false;
        bolGolpeAbajo=false;
        
        //Este contador me ayudara a cambiar el sprite de Link
        //cuando exista una colision
        tiempoCambioSprite=0;
        
        // posicion en 4 para que Link se mueva a la derecha
    	iDireccion = 4;
        VelY=0;
        VelX=1;
        
        // se posiciona a Link en alguna parte al azar del cuadrante 
        // superior izquierdo
	int iPosX = (int) (Math.random() *(getWidth() / 4));    
        int iPosY = (int) (Math.random() *(getHeight() / 4));    
       
        //A continuacion se cargan todas las imagenes que se usaran
        //en el juego

        URL urlImagenLinkDer = this.getClass().getResource("LinkDer.gif");
        URL urlImagenLinkDerGolpe = 
                                this.getClass().getResource("LinkDerGolpe.gif");
        URL urlImagenLinkIzq = this.getClass().getResource("LinkIzq.gif");
        URL urlImagenLinkIzqGolpe = 
                                this.getClass().getResource("LinkIzqGolpe.gif");
        URL urlImagenLinkArriba = this.getClass().getResource("LinkArriba.gif");
        URL urlImagenLinkArribaGolpe = 
                             this.getClass().getResource("LinkArribaGolpe.gif");
        URL urlImagenLinkAbajo = this.getClass().getResource("LinkAbajo.gif");
        URL urlImagenLinkAbajoGolpe = 
                             this.getClass().getResource("LinkAbajoGolpe.gif");
        
        //Convierto las URL images a Images
        imaImagenLinkDer = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkDer);
        imaImagenLinkDerGolpe = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkDerGolpe);
        imaImagenLinkIzq = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkIzq);
        imaImagenLinkIzqGolpe = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkIzqGolpe);
        imaImagenLinkArriba = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkArriba);
        imaImagenLinkArribaGolpe = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkArribaGolpe);
        imaImagenLinkAbajo = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkAbajo);
        imaImagenLinkAbajoGolpe = 
                    Toolkit.getDefaultToolkit().getImage(urlImagenLinkAbajoGolpe);
        
       
        // se crea el objeto de Link
	aniLink = new Animal(iPosX,iPosY,imaImagenLinkDer);
        

	//se crea el sonido de Link cuando se pega
	URL urlSonidoLink = this.getClass().getResource("LinkHit.wav");
        aucSonidoLink = getAudioClip (urlSonidoLink);

        // se define el background en color gris
	setBackground (Color.gray);
        /* se le añade la opcion al applet de ser escuchado por los eventos
           del teclado  */
	
        
        addKeyListener(this);
        
        
    }
	
    /** 
     * start
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
    public void start () {
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
    }
	
    /** 
     * run
     * 
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones
     * de nuestro juego.
     * 
     */
    public void run () {
        /* mientras dure el juego, se actualizan posiciones de jugadores
           se checa si hubo colisiones para desaparecer jugadores o corregir
           movimientos y se vuelve a pintar todo
        */ 
        while (true) {
            actualiza();
            checaColision();
            repaint();
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
    }
	
    /** 
     * actualiza
     * 
     * Metodo que actualiza la posicion del objeto aniLink 
     * 
     */
    public void actualiza(){
        //Dependiendo de la Direccion de Link es hacia donde se mueve.
        switch(iDireccion) {
            case 1: { //se mueve hacia arriba
                if (VelY<0){
                    aniLink.setImagen(imaImagenLinkArriba);    
                }
                aniLink.setY(aniLink.getY() + VelY);
                break;    
            }
            case 2: { //se mueve hacia abajo
                if (VelY>0){
                    aniLink.setImagen(imaImagenLinkAbajo);
                }
                aniLink.setY(aniLink.getY() + VelY);
                break;    
            }
            case 3: { //se mueve hacia izquierda
                if (VelX<0){
                    aniLink.setImagen(imaImagenLinkIzq);    
                }
                aniLink.setX(aniLink.getX() + VelX);
                break;    
            }
            case 4: { //se mueve hacia derecha
                if (VelX>0){
                    aniLink.setImagen(imaImagenLinkDer);    
                }
                aniLink.setX(aniLink.getX() + VelX);
                break;    	
            }
        }
        //Los siguientes if y else if son los que verifican si hay colision
        //pues si existe, cambia la imagen a la correspondiente
        if (bolGolpeDer){
            aniLink.setImagen(imaImagenLinkIzqGolpe);
            tiempoCambioSprite++;
            if(tiempoCambioSprite==10){ //Si es 10, entonces procedo a cambiar
                                        //la imagen a la normal
               aniLink.setImagen(imaImagenLinkIzq);
               bolGolpeDer=false;
               tiempoCambioSprite=0;
            }
        }
        
        else if (bolGolpeIzq){
            aniLink.setImagen(imaImagenLinkDerGolpe);
            tiempoCambioSprite++;
            if(tiempoCambioSprite==10){
               aniLink.setImagen(imaImagenLinkDer);
               bolGolpeIzq=false;
               tiempoCambioSprite=0;
            }
        }
        
        else if (bolGolpeArriba){
            aniLink.setImagen(imaImagenLinkAbajoGolpe);
            tiempoCambioSprite++;
            if(tiempoCambioSprite==10){
               aniLink.setImagen(imaImagenLinkAbajo);
               bolGolpeArriba=false;
               tiempoCambioSprite=0;
            }
        }
        
        else if (bolGolpeAbajo){
            aniLink.setImagen(imaImagenLinkArribaGolpe);
            tiempoCambioSprite++;
            if(tiempoCambioSprite==10){
               aniLink.setImagen(imaImagenLinkArriba);
               bolGolpeAbajo=false;
               tiempoCambioSprite=0;
            }
        }
        
        
    }
	
    /**
     * checaColision
     * 
     * Metodo usado para checar la colision del objeto Link
     * con las orillas del <code>Applet</code>.
     * 
     */
    public void checaColision(){
        //Colision del Link con el Applet dependiendo a donde se mueve.
     //Si la velocidad vertical es menor a cero, se esta moviendo hacia 
        //arriba
        if (VelY<0){
            if(aniLink.getY() < 0) { // y esta pasando el limite
                    iDireccion = 2;     // se cambia la direccion para abajo
                    VelY=-VelY; //Hago que se regrese con la misma velocidad
                                //pero para en direccion opuesta
                    bolGolpeArriba=true;
                    aucSonidoLink.play();
            
                }
            }
            
        //Si la velocidad vertical es mayor a cero, se esta moviendo hacia 
        //abajo
        if (VelY>0){
            if(aniLink.getY() + aniLink.getAlto() > getHeight()) { // y esta pasando el limite
                    iDireccion = 1;     // se cambia la direccion para arriba
                    VelY=-VelY; //Hago que se regrese con la misma velocidad
                                //pero para en direccion opuesta
                    bolGolpeAbajo=true;
                    aucSonidoLink.play();
            
                }
            }
            
        //Si la velocidad horizontal es mayor a cero, se esta moviendo hacia 
        //la derecha
        if (VelX>0){
            if(aniLink.getX() + aniLink.getAncho() > getWidth()) { // y esta 
                                                            //pasando el limite
                    iDireccion = 3;     // se cambia la direccion para izquierda
                    VelX=-VelX; //Hago que se regrese con la misma velocidad, 
                                //pero para en direccion opuesta
                    bolGolpeDer=true;
                    aucSonidoLink.play();
            
                }
            }
            
        //Si la velocidad horizontal es menor a cero, se esta moviendo hacia 
        //la izquierda
        if (VelX<0){
            if(aniLink.getX() < 0) { // y esta pasando el limite
                    iDireccion = 4;     // se cambia la direccion para la derecha
                    VelX=-VelX; //Hago que se regrese con la misma velocidad
                                //pero para en direccion opuesta
                    bolGolpeIzq=true;
                    aucSonidoLink.play();
            
                }
            }
        
       
    }
	
    /**
     * update
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y 
     * define cuando usar ahora el paint
     * 
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void update (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }

        // Actualiza la imagen de fondo.
        graGraficaApplet.setColor (getBackground ());
        graGraficaApplet.fillRect (0, 0, this.getSize().width, 
                this.getSize().height);

        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paint(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }

    /**
     * keyPressed
     * 
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al dejar presionada
     * alguna tecla.
     * 
     * @param keyEvent es el <code>KeyEvent</code> que se genera en al presionar.
     * 
     */
    public void keyPressed(KeyEvent keyEvent) {
       
    }
    
    /**
     * keyTyped
     * 
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al presionar una 
     * tecla que no es de accion.
     * 
     * @param keyEvent es el <code>KeyEvent</code> que se genera en al presionar.
     * 
     */
    public void keyTyped(KeyEvent keyEvent){
    	// no hay codigo pero se debe escribir el metodo
    }
    
    /**
     * keyReleased
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al soltar la tecla.
     * 
     * @param keyEvent es el <code>KeyEvent</code> que se genera en al soltar.
     * 
     */
    public void keyReleased(KeyEvent keyEvent){
    	 // si presiono flecha para arriba
    
        if(keyEvent.getKeyCode() == KeyEvent.VK_UP) {    
                iDireccion = 1;  // cambio la dirección arriba
   
                VelY--; //Actualizo la velocidad Vertical
                if (VelY==0)
                    VelY=-1;
                VelX=0; //Reseteo la velocidad horizontal
        }
        // si presiono flecha para abajo
        else if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {    
                iDireccion = 2;   // cambio la direccion para abajo
                VelY++; //Actualizo la velocidad Vertical
                if (VelY==0)
                    VelY=1;
                VelX=0; //Reseteo la velocidad horizontal
        }
        // si presiono flecha a la izquierda
        else if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {    
                iDireccion = 3;   // cambio la direccion a la izquierda
                VelX--; //Actualizo la velocidad horizontal
                if (VelX==0)
                    VelX=-1;
                VelY=0; //Reseteo la velocidad vertical
        }
        // si presiono flecha a la derecha
        else if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT){    
                iDireccion = 4;   // cambio la direccion a la derecha
                VelX++; //Actualizo la velocidad horizontal
                if (VelX==0)
                    VelX=1;
                VelY=0; //Reseteo la velocidad vertical
        }
    }
    
    /**
     * paint
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada,
     * ademas que cuando la imagen es cargada te despliega una advertencia.
     * 
     * @param graDibujo es el objeto de <code>Graphics</code> usado para dibujar.
     * 
     */
    public void paint(Graphics graDibujo) {
        // si la imagen ya se cargo
        if (aniLink != null) {
                //Dibuja la imagen de dumbo en el Applet
                aniLink.paint(graDibujo, this);
        } // sino se ha cargado se dibuja un mensaje 
        else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
        }
    }
}