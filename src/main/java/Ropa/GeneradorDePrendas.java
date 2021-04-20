package Ropa;

import Excepciones.PrendaInvalidaException;

import javax.lang.model.type.NullType;
import java.util.Arrays;

public class GeneradorDePrendas {

  private Prenda prenda;


  public void crearNuevaPrenda(){
    prenda = new Prenda();
  }

  public Prenda getPrenda(){

    if(this.prenda.getTipo() == null ||
        this.prenda.getMaterialConstruccion() == null ||
        Arrays.stream(this.prenda.getColorPrincipal()).count() < 3){

      throw new PrendaInvalidaException("La prenda generada no es valida. Debe tener tipo de prenda, material de construccion y color primario");
    }

    return prenda;
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
