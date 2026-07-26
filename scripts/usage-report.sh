#!/usr/bin/env bash
# [Staff only] 研修 IAM ユーザー別の Bedrock 呼び出し回数を CloudTrail から集計する。
# CloudTrail のイベント履歴（設定不要・90日保存）を使うため、事前準備は不要。
# 使い方: AWS_PROFILE=<admin-profile> scripts/usage-report.sh [開始日 YYYY-MM-DD] [終了日 YYYY-MM-DD]
# 例:     AWS_PROFILE=admin scripts/usage-report.sh 2026-08-01           # その日1日分
#         AWS_PROFILE=admin scripts/usage-report.sh 2026-08-01 2026-08-03 # 期間指定
set -euo pipefail
START="${1:-$(date +%F)}"
END="${2:-$START}"
REGION="${AWS_REGION:-ap-northeast-1}"

echo "# Bedrock 呼び出し回数（$START 〜 $END JST / region=$REGION）"
for EV in InvokeModel InvokeModelWithResponseStream Converse ConverseStream; do
  aws cloudtrail lookup-events --region "$REGION" \
    --lookup-attributes "AttributeKey=EventName,AttributeValue=$EV" \
    --start-time "${START}T00:00:00+09:00" --end-time "${END}T23:59:59+09:00" \
    --query 'Events[].Username' --output text
done | tr '\t' '\n' | grep '^genai-training-' | sort | uniq -c | sort -rn \
  || echo "(該当期間に genai-training-* の呼び出しなし)"
