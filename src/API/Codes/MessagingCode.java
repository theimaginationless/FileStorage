package API.Codes;

import API.Messaging.MessageExtractors.MessageExtractor;
import API.Messaging.MessageExtractors.OffsetMessageExtractor;
import API.Messaging.MessageExtractors.WriteFileRequestMessageExtractor;
import API.Messaging.MessageExtractors.WriteFileResponseMessageExtractor;

public enum MessagingCode {
    GETOFFSET {
        @Override
        public MessageExtractor getInstance() {
            return new OffsetMessageExtractor();
        }
    },

    WRITEFILE_REQUEST {
        @Override
        public MessageExtractor getInstance() {
            return new WriteFileRequestMessageExtractor();
        }
    },

    WRITEFILE_RESPONSE {
        @Override
        public MessageExtractor getInstance() {
            return new WriteFileResponseMessageExtractor();
        }
    };

    public abstract MessageExtractor getInstance();
}
