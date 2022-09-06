import Checkmark from "../../General/Checkmark";
import Loading from "../../Loading";
import {isCompleted} from "./ResultsList";

export default function Status({ placementResult }) {
    if ( isCompleted( placementResult.status ) ) {
        return <Checkmark checked={true} />;
    }

    return <Loading block={false} show={true} size="sm" />;
}