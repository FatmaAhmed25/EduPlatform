export interface Announcement {
    id: number;
    title: string;
    content: string;
    fileName?: string;
    showComments?: boolean;
    comments: Comment[];
    // Add other necessary fields if there are more
  }
  