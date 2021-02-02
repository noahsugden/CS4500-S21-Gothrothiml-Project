/**
 * Class representing the Addition operation used to calculate the sum of each numJSON value
 */
public class Addition extends Operation {

  /**
   * Constructor for the Addition Operator that sets the identity as 0
   */
  public Addition() {
    this.setIdentity(0);
  }

  /**
   * Method representing the addition of the previous numJSON value, and the current numJSON
   * element
   *
   * @param element additional element to be added
   * @param result  previous total result
   */
  @Override
  public int calculateJson(int result, int element) {
    //simply add given previous result, to the additional element
    return result + element;
  }
}
