import groovyx.net.http.HTTPBuilder

class MessageRemover {
    // TODO Drop your Slack API token into here for this all the work
    String token = "xxxx-##########-##########-##########-xxxxxx"

    final String BASE = "https://slack.com"
    HTTPBuilder http

    MessageRemover() {
        this(null)
    }

    MessageRemover(String token) {
        http = new HTTPBuilder(BASE)

        if (token) {
            println("Using token provided from cmd")
            this.token = token
        }
    }

    List<Im> getIms() {
        def http = new HTTPBuilder(BASE)
        List<Im> ims = []

        try {
            def res = http.get(path: "/api/im.list", query: [token: token])

            if (!res.ok) {
                throw new Exception(res.error as String)
            }

            res.ims.each {
                ims << new Im(
                        id: it.id,
                        created: new Date(it.created as Long),
                        isIm: it.is_im as Boolean,
                        user: it.user,
                        isUserDeleted: it.is_user_deleted as Boolean
                )
            }

            ims.each {
                res = http.get(path: "/api/users.info", query: [token: token, user: it.user])
                it.name = res.user.name
            }
        } catch (Exception e) {
            println("IM call failed: ${e.message}")
            throw e
        }

        return ims
    }

    void printIms(List<Im> ims) {
        printf("%3s | %16s | %16s | %24s\n", "#", "IM ID", "USER", "NAME")

        ims.eachWithIndex { it, i ->
            printf("%3d | %16s | %16s | %24s\n", i, it.id, it.user, it.name)
        }
    }

    void manipulateIms(List<Im> ims) {
        println("What would you like to do?")
        println("'view' or 'delete' IM ID, or print IMs again with 'ims'")
        println("E.g. view USLACKBT, or delete USLACKBT, or ims")

        BufferedReader stdin = System.in.newReader()
        String input

        while (true) {
            input = stdin.readLine()

            if (input == null) {
                break
            }

            try {
                String[] args = input.split("\\s")

                if (args[0].toLowerCase() == "view") {
                    String imId

                    try {
                        imId = args[1]

                    } catch (Exception ignored) {
                        throw new Exception("Please provide the IM ID")
                    }

                    Boolean validIm = false
                    for (Im im : ims) {
                        if (im.id == imId) {
                            validIm = true
                            break
                        }
                    }

                    if (!validIm) {
                        throw new Exception("IM not valid")
                    }

                    try {
                        def res = http.get(path: "/api/im.history", query: [pretty: '1', channel: imId, count: 10, token: token])

                        printf("%16s | %32s | %64s\n", "User", "Timestamp", "Message")

                        res.messages.each {
                            printf("%16s | %32s | %64s\n", it.user, it.ts, it.text)
                        }
                    } catch (Exception e) {
                        throw new Exception("History call failed: ${e.message}")
                    }

                } else if (args[0].toLowerCase() == "delete") {
                    // TODO Merge this with what's in the first if block. Too repetitive (although this was meant to be throw away code)
                    String imId

                    try {
                        imId = args[1]

                    } catch (Exception ignored) {
                        throw new Exception("Please provide the IM ID")
                    }

                    Im im = null
                    for (Im imItr : ims) {
                        if (imItr.id == imId) {
                            im = imItr
                            break
                        }
                    }

                    if (!im) {
                        throw new Exception("IM not valid")
                    }

                    try {
                        def res = http.get(path: "/api/im.history", query: [channel: imId, count: 1000, token: token])

                        res.messages.each {
                            if (it.user != im.user) {
                                // I.e. me, not the other user, since I can only delete my own messages
                                def deleteAttempt = http.get(path: "/api/chat.delete", query: [channel: im.id, ts: it.ts, as_user: true, token: token])
                                println(deleteAttempt)
                            }
                        }
                    } catch (Exception e) {
                        throw new Exception("Message delete failed: ${e.message}")
                    }

                } else if (args[0].toLowerCase() == "ims") {
                    printIms(ims)

                } else if (args[0].toLowerCase() == "bye") {
                    throw new InterruptedException()

                } else if (args[0].toLowerCase() == "exit") {
                    throw new InterruptedException()

                } else {
                    println("You argument is invalid")
                }
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    println("K thx bye")
                    break

                } else {
                    println(e.message)
                }
            }

            println("What next? >")
        }
    }

    static void main(String[] args) {
        MessageRemover messageRemover

        if (args.length > 0) {
            messageRemover = new MessageRemover(args[0])

        } else {
            messageRemover = new MessageRemover()
        }

        println("Please wait...")
        List<Im> ims = messageRemover.ims

        println("Active IMs")
        messageRemover.printIms(ims)
        messageRemover.manipulateIms(ims)
    }

    /**
     * POJO (or POGO in this case) to retain IM/DM details in memory
     */
    class Im {
        String id
        Date created
        Boolean isIm
        String user
        Boolean isUserDeleted

        String name
    }
}
