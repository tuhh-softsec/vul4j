package Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import Ropa.*;

public class RopaTest {

  @Test
  public void siComeAumentaSuEnergia() {
    Golondrina pepita = new Golondrina(40);
    pepita.comer(23);
    Assertions.assertEquals(109, pepita.getEnergia());
  }

  @Test
  public void siVuelaDecrementaSuEnergia() {
    Golondrina pepita = new Golondrina(40);
    pepita.comer(23);
    pepita.volar();
    Assertions.assertEquals(99, pepita.getEnergia());
  }
}
