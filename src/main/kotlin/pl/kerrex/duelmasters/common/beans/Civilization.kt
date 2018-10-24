package pl.kerrex.duelmasters.common.beans

enum class Civilization {
    FIRE,
    WATER,
    NATURE,
    DARKNESS,
    LIGHT;

    override fun toString(): String{
        return this.name.toLowerCase()
    }


}