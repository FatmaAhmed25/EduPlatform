export interface Announcement {
    id: number;
    title: string;
    content: string;
    fileName?: string;
    showComments?: boolean;
    comments: Comment[];
    createdAt:string;
  }
  