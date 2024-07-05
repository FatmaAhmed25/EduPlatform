  import { UserCommentDTO } from "./UserCommentDTO.model";
  
  export interface getCommentDTO {
    commentId: number;
    userCommentDTO: UserCommentDTO;
    content: string;
    createdAt: string;
  }
  