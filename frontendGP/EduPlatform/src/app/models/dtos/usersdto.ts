export interface UserDTO {
    userId:number;
    username: string;
    email: string;
    password: string;
    bio:string;
    userType:string;
    editing?: boolean;
  }
  
  export interface UpdateUserDTO {
    userId: number;
    username?: string;
    email?: string;
    password?: string;
    bio?: string;
  }
  