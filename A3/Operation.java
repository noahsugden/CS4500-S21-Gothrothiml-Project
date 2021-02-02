import com.google.gson.*;

/**
 * Class representing the operations that can be applied to the numJSONs
 */
public abstract class Operation {
  public int identity;

  /**
   * Method that sets an identity number based on the Operation
   *
   * @param identity the number that corresponds with the identity function of this operation
   */
  public void setIdentity(int identity) {
    this.identity = identity;
  }

  /**
   * Abstract method that calculates either a sum or product for a NumJSON
   */
  public abstract int calculateJson(int result, int element);

  /**
   * Method that handles the various types of numJSONs and calculates them according to the
   * initalized identity value that correlates with sum and product
   *
   * @param j the NumJSON we want to calculate the total for
   */
  public int jsonMath(JsonElement j) {
    int result = this.identity;
    // if json is primitive, initialize it as a primitive
    if (j.isJsonPrimitive()) {
      JsonPrimitive prim = j.getAsJsonPrimitive();
      // if the primitive is a String, we simply return the identity (0 for sum, 1 for product)
      if (prim.isString()) {
        return result;
      } else if (prim.isNumber()) {
        // if the primitive is an int/number, we can just directly add the number to the overall result
        result = calculateJson(result, prim.getAsInt());
      }
    } else if (j.isJsonArray()) {
      JsonArray arr = j.getAsJsonArray();
      // if the numJSON is an array, we just parse through each element, running jsonMath on each element
      // to initialize the total
      for (JsonElement arrElement : arr) {
        result = calculateJson(result, jsonMath(arrElement));
      }
    } else if (j.isJsonObject()) {
      // if the numJSON is an object, we search for the "payload" key, and if it is not a null value, we
      // run jsonMath on the given payload values
      JsonElement payload = j.getAsJsonObject().get("payload");
      if (!payload.isJsonNull()) {
        result = calculateJson(result, jsonMath(payload));
      }
    }
    // return the total result of the numJSONs
    return result;
  }
}

