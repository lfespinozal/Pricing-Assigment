/**
 * Author: Rod Madden
 * Date: 4/9/2021
 * Purpose: PriceFX homework challenge
 */

// ---------------------------
//
// HOMEWORK
//
// Use Groovy to write code under "YOUR CODE GOES BELOW THIS LINE" comment.
// Use Groovy closures whenever possible
// You should develop and present your code in IntelliJ Idea ( Community Edition )
// Do not over-engineer the solution.
//
// Assume you got some data from a customer - your task is to design a routine that will calculate the average Product price per GROUP.
//
// The Price of each Product is calculated as:
// Cost * (1 + Margin)
// If, however, the Product Category evaluates to a value greater than "C3" ( i.e. C4, C5, etc. ) then you must add 10% to
// the product cost before adding the margin: ( Cost * 1.1 ) * (1 + Margin)
//
// Assume there can be a large number of products.
//
// Plus points:
// - use Groovy closures ( wherever it makes sense )
// - make the category look-up performance effective

// contains information about [Product, Group, Cost] ...example ["A", "G1", 20.1],
def products = [
        ["A", "G1", 20.1],
        ["B", "G2", 98.4],
        ["C", "G1", 49.7],
        ["D", "G3", 35.8],
        ["E", "G3", 105.5],
        ["F", "G1", 55.2],
        ["G", "G1", 12.7],
        ["H", "G3", 88.6],
        ["I", "G1", 5.2],
        ["J", "G2", 72.4]]

// contains information about Category classification based on product Cost
// [Category, Cost range from (inclusive), Cost range to (exclusive)]
// i.e. if a Product has Cost between 0 and 25, it belongs to category C1
// ranges are mutually exclusive and the last range has a null as upper limit.
def category = [
        ["C3", 50, 75],
        ["C4", 75, 100],
        ["C2", 25, 50],
        ["C5", 100, null],
        ["C1", 0, 25]]

// contains information about margins for each product Category
// [Category, Margin (either percentage or absolute value)]
def margins = [
        "C1": "20%",
        "C2": "30%",
        "C3": "0.4",
        "C4": "50%",
        "C5": "0.6"]

// ---------------------------
//
// YOUR CODE GOES BELOW THIS LINE
//
// Assign the 'result' variable so the assertion at the end validates
//
// ---------------------------

class CategoryClass {
    String category
    Double minCost
    Double maxCost
}

def categoryList = [new CategoryClass()]

category.each(c -> {
    def n = 0
    def newCategory = new CategoryClass()

    c.each(categoryData -> {
        switch (n) {
            case 0:
                newCategory.setCategory(categoryData.toString())
                break
            case 1:
                newCategory.setMinCost((Double) categoryData)
                break
            case 2:
                if(categoryData == null) {
                    newCategory.setMaxCost(10000)
                    break
                }
                newCategory.setMaxCost((Double) categoryData)
                break
        }
        n++
    })
    categoryList.add(newCategory)
})

def actualMargin = [:]

margins.each((margin, value) -> {

    def stringValue = margins[margin]

    if (stringValue.contains("%")) {
        stringValue = stringValue.replace("%", "")
        actualMargin[margin] = Double.parseDouble(stringValue) / 100
    } else {
        actualMargin[margin] = Double.parseDouble(stringValue)
    }
})

def resultMap = [:]
def numberProducts = [:]
def greaterCategory = "C3"

products.each(product -> {

    def n = 0
    def group = ""

    product.each(dataProduct -> {

        if (n == 1) {
            group = dataProduct.toString()
            if (!resultMap.keySet().contains(group)) {
                resultMap[group] = 0
                numberProducts[group] = 0
            }
            numberProducts[group] += 1
        }

        if (n == 2) {
            def cost = (Double) dataProduct
            def categoryGroup = categoryList.stream()
                    .filter(c -> c.getMinCost() < cost && cost < c.getMaxCost())
                    .findFirst().get().getCategory()

            if(categoryGroup > greaterCategory)
                //( Cost * 1.1 ) * (1 + Margin)
                resultMap[group] += (cost * 1.1) * (1 + (Double) actualMargin[categoryGroup])
            else
                //Cost * (1 + Margin)
                resultMap[group] += cost * (1 + (Double) actualMargin[categoryGroup])
        }
        n++
    })
})

resultMap.each ((group , value) -> {
    def result = resultMap[group] / numberProducts[group]
    def roundedResult = Math.round((Double) result * Math.pow(10, 1)) / Math.pow(10, 1)
    resultMap[group] = roundedResult
})

// ---------------------------
//
// IF YOUR CODE WORKS, YOU SHOULD GET "It works!" WRITTEN IN THE CONSOLE
//
// ---------------------------

// Assert appropriate answer
assert resultMap == [
        "G1": 37.5,
        "G2": 131.9,
        "G3": 126.1
]: "It doesn't work"

println "It works!"
