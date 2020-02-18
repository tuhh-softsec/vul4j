package vn.mavn.patientservice.response;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Response with page.
 *
 * @param <T> the type parameter
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseWithPage<T extends Serializable> {

  private List<T> data;
  private Long totalElement;
  private int totalPage;
  private int pageIndex;

}

