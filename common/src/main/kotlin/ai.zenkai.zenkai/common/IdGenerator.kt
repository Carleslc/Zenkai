package ai.zenkai.zenkai.common

class IdGenerator(start: Long = 0) {

    var last = start - 1
    
    fun getNext() = ++last
    
    fun getNextString() = getNext().toString()

}