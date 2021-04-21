package Ropa;

import Excepciones.FaltaTipoDePrendaException;
import Excepciones.PrendaInvalidaException;

public class GeneradorDePrendas {

  private Prenda prenda = new Prenda();


  public void configurarNuevaPrenda(){
    prenda = new Prenda();
  }

  public Prenda getPrenda(){

    if(this.prenda.getTipo() == null ||
        this.prenda.getMaterialConstruccion() == null ||
        this.prenda.getColorPrincipal() == null){

      throw new PrendaInvalidaException("La prenda generada no es valida. Debe tener tipo de prenda, material de construccion y color primario");
    }

    return prenda;
  }



  public CategoriaPrenda identificarCategoria(){

    if(this.prenda.getTipo() == null){
      throw new FaltaTipoDePrendaException("Para Identificar la CATEGORIA de tu prenda primero es necesario ingresar su TIPO");
    }
    return prenda.getCategoria();
  }

  public void setTipoConCategoria(String tipoPrenda){
    CategoriaPrenda categoria = RepositorioTipoPrendas.instance().buscarCategoria(tipoPrenda);
    prenda.setTipo(tipoPrenda);
    prenda.setCategoria(categoria);
  }

  public void setMaterialConstruccion(String materialConstruccion){
    prenda.setMaterialConstruccion(materialConstruccion);
  }

  public void setColorPrincipal(int color1, int color2, int color3){
    prenda.setColorPrincipal(color1, color2, color3);
  }

  public void setColorSecundario(int color1, int color2, int color3){
    prenda.setColorSecundario(color1, color2, color3);
  }
}
