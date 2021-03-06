package handler.messages;

import Entity.Message;
import handler.RequsetHandlerBase;
import org.jetbrains.annotations.NotNull;
import reply.GetMessagesReply;
import reply.Reply;
import request.Request;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class GetMessagesRequestHandler extends RequsetHandlerBase {

    private static final String OFFSET = "offset";
    private static final String COUNT = "count";

    @Override
    protected boolean isAllowedMethod(Request request) {
        return request.getMethod().equals("GET");
    }

    @Override
    protected boolean isRequestValid(Request request) {
        query = request.getQueryMap();
        if (query == null) {
            return false;
        }
        return isQueryValid();
    }

    private boolean isQueryValid() {
        try {
            offset = Integer.valueOf(query.get(OFFSET).getFirst());
            count = Integer.valueOf(query.get(COUNT).getFirst());
        } catch (Exception e) {
            logger.warn("error while parsing query");
            return false;
        }
        return offset >= 0 && count >= 0;
    }

    @Override
    protected @NotNull Reply createReply(Request request) {
        GetMessagesReply reply = new GetMessagesReply();
        List<Message> messageList = messageDAO.getMessages(message -> message.getId() >= offset)
                .stream()
                .limit(count)
                .collect(Collectors.toList());
        reply.setMessages(messageList);
        return reply;
    }

    private Map<String, Deque<String>> query;
    int offset = 0;
    int count = 0;
}
