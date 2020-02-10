package API.Codes;

import API.Messaging.MessageExtractors.MessageExtractor;
import API.Messaging.MessageExtractors.OffsetMessageExtractor;

public enum MessagingCode {
    GETOFFSET {
        @Override
        public MessageExtractor getInstance() {
            return new OffsetMessageExtractor();
        }
    };

    public abstract MessageExtractor getInstance();
}
