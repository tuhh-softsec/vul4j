package Ropa;

public class Prenda {

  private String tipo;
  private CategoriaPrenda categoria;
  private String materialConstruccion;
  private int[] colorPrincipal = new int[3];
  private int[] colorSecundario = new int[3];

  public Prenda(){}

  //SETTERS
  public void setTipo(String tipo){
    this.tipo = tipo;
  }

  public void setCategoria(CategoriaPrenda categoria){
    this.categoria = categoria;
  }

  public void setMaterialConstruccion(String materialConstruccion){
    this.materialConstruccion = materialConstruccion;
  }

  public void setColorPrincipal(int color1, int color2, int color3){
    this.colorPrincipal[0] = color1;
    this.colorPrincipal[1] = color2;
    this.colorPrincipal[2] = color3;
  }

  public void setColorSecundario(int color1, int color2, int color3){
    this.colorSecundario[0] = color1;
    this.colorSecundario[1] = color2;
    this.colorSecundario[2] = color3;
  }


  //GETTERS
  public String getTipo() {
    return tipo;
  }

  public CategoriaPrenda getCategoria() {
    return categoria;
  }

  public String getMaterialConstruccion() {
    return materialConstruccion;
  }

  public int[] getColorPrincipal() {
    return colorPrincipal;
  }

  public int[] getColorSecundario() {
    return colorSecundario;
  }
}
