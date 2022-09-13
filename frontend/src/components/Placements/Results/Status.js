import Checkmark from "../../General/Checkmark";
import Loading from "../../Loading";
import {isCompleted} from "./ResultsList";

export default function Status({ placementResult }) {
    if ( isCompleted( placementResult.status ) ) {
        return <Checkmark checked={true} />;
    }

    if ( placementResult.status === 'FAILED' ) {
        return <Checkmark checked={false} />;
    }

    return <Loading block={false} show={true} size="sm" />;
}